package com.fujitsu.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * 实际取色的时候一定要16位的，这样比较准确
 * 
 * @author new
 */
public class CreateLineChart {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db=dbf.newDocumentBuilder();  
		Document doc = db.parse(new File("D:\\Program Files\\tomcat\\Tomcat7\\temp\\resultTemp.xml"));
		
		NodeList nodeList = doc.getElementsByTagName("data"); 
		System.out.println("data节点链的长度:" + nodeList.getLength()); 
		
		String[] x_value = new String[nodeList.getLength()];
		double[] y_value = new double[nodeList.getLength()];
		
		for(int j=0;j<nodeList.getLength();j++){
			Node fatherNode = nodeList.item(j);
			NamedNodeMap attributes = fatherNode.getAttributes();  
			
			Node attribute_x = attributes.item(0);
			x_value[j] = attribute_x.getNodeValue();
			
			Node attribute_y = attributes.item(1);
			y_value[j] = Double.parseDouble(attribute_y.getNodeValue());
			
		}
		
		
		CreateLineChart pm = new CreateLineChart();
		// 生成折线图
		String CHART_PATH = "D:\\test\\";
		pm.makeLineAndShapeChart(x_value,y_value,"RG20121220165733_20121221104446",CHART_PATH);
	}
	public void drowOTDRChart(String pathOnly,String fileNameOnly,String xmlPath){
		String CHART_PATH = pathOnly;
		String fileName = fileNameOnly.substring( 0,fileNameOnly.length()-4);
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new File(xmlPath));
		}  catch (ParserConfigurationException e) {
			e.printStackTrace();
		}  catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		NodeList nodeList = doc.getElementsByTagName("data"); 
//		System.out.println("data节点链的长度:" + nodeList.getLength()); 
		String[] x_value = new String[nodeList.getLength()];
		double[] y_value = new double[nodeList.getLength()];
		
		for(int j=0;j<nodeList.getLength();j++){
			Node fatherNode = nodeList.item(j);
			NamedNodeMap attributes = fatherNode.getAttributes();  
			
			Node attribute_x = attributes.item(0);
			x_value[j] = attribute_x.getNodeValue();
			
			Node attribute_y = attributes.item(1);
			y_value[j] = Double.parseDouble(attribute_y.getNodeValue());
			
		}
		
		
		CreateLineChart pm = new CreateLineChart();
		// 生成折线图
		pm.makeLineAndShapeChart(x_value,y_value,fileName,CHART_PATH);
		
	}
	/**
	 * 生成折线图
	 */
	public void makeLineAndShapeChart(String[] x_value,double[] y_value,String fileName,String CHART_PATH) {
		double[][] data = new double[][] { y_value };
		String[] rowKeys = { "" };
		String[] columnKeys = x_value;
		CategoryDataset dataset = getBarData(data, rowKeys, columnKeys);
		createTimeXYChar("折线图", "X", "Y", dataset, fileName+".png",CHART_PATH);
	}
	// 折线图 数据集
	public CategoryDataset getBarData(double[][] data, String[] rowKeys,
			String[] columnKeys) {
		return DatasetUtilities
				.createCategoryDataset(rowKeys, columnKeys, data);
	}
	/**
	 * 判断文件夹是否存在，如果不存在则新建
	 * 
	 * @param chartPath
	 */
	private void isChartPathExist(String chartPath) {
		File file = new File(chartPath);
		if (!file.exists()) {
			file.mkdirs();
			// log.info("CHART_PATH="+CHART_PATH+"create.");
		}
	}
	/**
	 * 折线图
	 * 
	 * @param chartTitle
	 * @param x
	 * @param y
	 * @param xyDataset
	 * @param charName
	 * @return
	 */
	public String createTimeXYChar(String chartTitle, String x, String y,
			CategoryDataset xyDataset, String charName,String CHART_PATH) {
		JFreeChart chart = ChartFactory.createLineChart(chartTitle, x, y,
				xyDataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setTextAntiAlias(false);
		chart.setBackgroundPaint(Color.WHITE);
		// 设置图标题的字体重新设置title
		Font font = new Font("隶书", Font.BOLD, 25);
		TextTitle title = new TextTitle(chartTitle);
		title.setFont(font);
		chart.setTitle(title);
		// 设置面板字体
		Font labelFont = new Font("SansSerif", Font.TRUETYPE_FONT, 12);
		chart.setBackgroundPaint(Color.WHITE);
		CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
		// x轴 // 分类轴网格是否可见
		categoryplot.setDomainGridlinesVisible(false);
		// y轴 //数据轴网格是否可见
		categoryplot.setRangeGridlinesVisible(true);
		categoryplot.setRangeGridlinePaint(Color.darkGray);// 虚线色彩
		categoryplot.setDomainGridlinePaint(Color.lightGray);// 虚线色彩
		categoryplot.setBackgroundPaint(Color.WHITE);
		// 设置轴和面板之间的距离
		// categoryplot.setAxisOffset(new RectangleInsets(5D, 5D, 5D, 5D));
		CategoryAxis domainAxis = categoryplot.getDomainAxis();
		domainAxis.setLabelFont(labelFont);// 轴标题
		domainAxis.setTickLabelFont(labelFont);// 轴数值
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45); // 横轴上的
		// Lable
		// 45度倾斜
		// 设置距离图片左端距离
		domainAxis.setLowerMargin(0.0);
		// 设置距离图片右端距离
		domainAxis.setUpperMargin(0.0);
		
		domainAxis.setMaximumCategoryLabelWidthRatio(0.6f);
		NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberaxis.setAutoRangeIncludesZero(true);
		// 获得renderer 注意这里是下嗍造型到lineandshaperenderer！！
		LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot
				.getRenderer();
		lineandshaperenderer.setBaseShapesVisible(false); // series 点（即数据点）可见
		lineandshaperenderer.setBaseLinesVisible(true); // series 点（即数据点）间有连线可见
		lineandshaperenderer.setSeriesStroke(0, new BasicStroke(2.0F));
		// 显示折点数据
		// lineandshaperenderer.setBaseItemLabelGenerator(new
		// StandardCategoryItemLabelGenerator());
		// lineandshaperenderer.setBaseItemLabelsVisible(true);
		FileOutputStream fos_jpg = null;
		try {
			isChartPathExist(CHART_PATH);
			String chartName = CHART_PATH + charName;
			fos_jpg = new FileOutputStream(chartName);
			// 将报表保存为png文件
			ChartUtilities.writeChartAsPNG(fos_jpg, chart, 1300, 600);
//			ChartUtilities.writeChartAsJPEG(fos_jpg, chart, 1500, 800);
			return chartName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				fos_jpg.close();
//				System.out.println("create time-createTimeXYChar.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}