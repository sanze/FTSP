package com.fujitsu.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;
import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;

import com.fujitsu.handler.ExceptionHandler;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.spi.orb.ORB;


public class ExcelWriterUtil {
	protected final String FILEPATH = System.getProperty("user.dir")+"/OutPutFiles/";
	protected static Logger log = Logger.getLogger(ExcelWriterUtil.class);
	protected WritableFont BoldFont = new WritableFont(WritableFont.ARIAL, 14,
			WritableFont.BOLD);
	protected WritableCellFormat wcf_title = new WritableCellFormat(BoldFont);
	protected File file;

	protected String encode = "GB2312";
	protected String seprate = "**********************************seprate**************************************";

	protected ArrayList<Label> labelList = new ArrayList<Label>();
	private int rowNum = 0;
	

	public ExcelWriterUtil(String encode){
		this.encode = encode;
		try {
			wcf_title.setBorder(Border.ALL, BorderLineStyle.THIN);
			wcf_title.setVerticalAlignment(VerticalAlignment.TOP);
			wcf_title.setAlignment(Alignment.LEFT);
			wcf_title.setWrap(false);
			
			SimpleDateFormat formatter = new SimpleDateFormat ("yyyy_MM_dd_HH-mm-ss");
			String curDate = formatter.format(new Date(System.currentTimeMillis()));
			File dirFile = null;
			dirFile = new File(FILEPATH);
			if (!(dirFile.exists())&&!(dirFile.isDirectory())){
                boolean creadok  =  dirFile.mkdirs();
                if (creadok) {
                   System.out.println( " ok:文件已创建！" );
               } else {
                   System.out.println( " err:文件创建失败！" );                    
               } 
           }
			file = new File(FILEPATH+curDate+".xls");
			WritableWorkbook book = Workbook.createWorkbook(file);
			book.createSheet("sheet1", 0);
			book.getSheet(0).setHidden(true);
			book.write();
			book.close();
		} catch (WriteException e) {
			ExceptionHandler.handleException(e);
		} catch (IOException e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	public ExcelWriterUtil(String filePath, String encode){
		this.encode = encode;
		try {
			wcf_title.setBorder(Border.ALL, BorderLineStyle.THIN);
			wcf_title.setVerticalAlignment(VerticalAlignment.TOP);
			wcf_title.setAlignment(Alignment.LEFT);
			wcf_title.setWrap(false);
			
			//SimpleDateFormat formatter = new SimpleDateFormat ("yyyy_MM_dd_HH-mm-ss");
			//String curDate = formatter.format(new Date(System.currentTimeMillis()));
			file = new File(FILEPATH+filePath+".xls");
			File dirFile = null;
			dirFile = new File(file.getParent());
			if (!(dirFile.exists())||!(dirFile.isDirectory())){
                boolean creadok  =  dirFile.mkdirs();
                if (creadok) {
                   System.out.println( " ok:文件已创建！" );
               } else {
                   System.out.println( " err:文件创建失败！" );                    
               } 
            }
			WritableWorkbook book = Workbook.createWorkbook(file);
			book.createSheet("sheet1", 0);
			book.getSheet(0).setHidden(true);
			book.write();
			book.close();
		} catch (WriteException e) {
			ExceptionHandler.handleException(e);
		} catch (IOException e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	/**
	 * 写入List中的单元格
	 * 
	 * @param labelList
	 * @param ws
	 */
	private void addCell(ArrayList<Label> labelList, WritableSheet ws) {
		for (int i = 0; i < labelList.size(); i++) {
			try {
				if(labelList.get(i).getColumn()>255){
					log.error("列数超过255限制");
					continue;
				}
				if(labelList.get(i).getRow()>65535){
					log.error("行数超过65535限制");
					return;
				}
				
				ws.addCell(labelList.get(i));
			} catch (RowsExceededException e) {
				ExceptionHandler.handleException(e);
			} catch (WriteException e) {
				ExceptionHandler.handleException(e);
			}
		}
	}

	/**
	 * 转码
	 * 
	 * @param value
	 * @return
	 */
	private String Stringformat(String value) {
		try {
			return new String(value.getBytes("ISO8859_1"), encode);
		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.handleException(e);
		}
		return "";
	}

	public void writeExcel(Object o, String title, int sheetNo) {
		writeExcel(o,title,title,sheetNo);
	}
	/**
	 * 利用反射打印传入的任何对象
	 * 
	 * @param o
	 *            需要打印的对象
	 * @param title
	 *            表标题
	 * @param sheetNo
	 *            表页数
	 */
	public void writeExcel(Object o, String title, String sheetName, int sheetNo) {
		
		try {
			if(sheetNo>255){
				log.error("sheet数超过255限制");
				return;
			}
			// 初始化sheet
			labelList.clear();
			rowNum = 0;
			Workbook workbook = Workbook.getWorkbook(file);
			WritableWorkbook book = Workbook.createWorkbook(file, workbook);
			WritableSheet ws = book.createSheet(sheetName, sheetNo);

			labelList.add(new Label(0, rowNum, title, wcf_title));
			rowNum++;
			if(o == null){
				labelList.add(new Label(1, rowNum++, "异常：数据为NULL"));
			}else{
				// 利用反射分析打印
				analyzeListObject(null, o, 1);
			}

			addCell(labelList, ws);
			book.write();
			book.close();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			log.error(e, e.fillInStackTrace());
		}
	}

	/**
	 * 数组和ArrayList分析入口
	 * 
	 * @param o
	 * @param rowNum
	 */
	private void analyzeListObject(String name, Object o, int callCount) {
		if (o != null) {
			if (o.getClass().isArray()) {
				// 若为数组
				if (o.getClass().getComponentType().isPrimitive()) {
					// 如果是基本数据类型数组那么它就不能被转换为Object[]好烦啊
					analyseArray(name, o, callCount);
				} else {
					Object[] datas = (Object[]) o;
					for (int i = 0; i < datas.length; i++) {
						if (name == null) {
							// 第一次调用加分隔符
							labelList.add(new Label(1, rowNum++, seprate));
							// 分析Array中每个对象
							analyzeObject(datas[i], callCount);
							rowNum++;
						} else {
							labelList.add(new Label(callCount-1, rowNum++, name + "[" + i
									+ "]:"));
							// 分析Array中每个对象
							analyzeObject(datas[i], callCount);
						}
					}
					if (name == null) {
						// 最外层调用加统计值
						labelList.add(new Label(0, rowNum, "总数量为："
								+ datas.length + "条"));
					}
				}
			} else if (o.getClass().isAssignableFrom(List.class)||o.getClass().isAssignableFrom(ArrayList.class)) {
				// 若为List
				List datas = (List) o;
				// 目测IDL包里面没用到基本数据类型的ArrayList,暂时先不管他
				for (int i = 0; i < datas.size(); i++) {
					if (name == null) {
						// 第一次调用加分隔符
						labelList.add(new Label(1, rowNum++, seprate));
						// 分析ArrayList中每个对象
						analyzeObject(datas.get(i), callCount);
						rowNum++;
					} else {
						labelList.add(new Label(callCount-1, rowNum++, name + ".get[" + i
								+ "]:"));
						// 分析ArrayList中每个对象
						analyzeObject(datas.get(i), callCount);
					}
				}
				if (name == null) {
					// 最外层调用加统计值
					labelList.add(new Label(0, rowNum, "总数量为：" + datas.size()
							+ "条"));
				}
			} else {
				// 若为对象
				analyzeObject(o, callCount);
				if (name == null) {
					// 最外层调用加统计值
					labelList.add(new Label(0, rowNum, "总数量为：1条"));
				}
			}
		} else {
			// 如果首次执行时数据为空
			if (name == null) {
				labelList.add(new Label(0, rowNum, "没有取得数据，返回值为null！"));
			} else {
				// null全部跳过
				// labelList.add(new Label(1, rowNum++, name + " is: null"));
			}
		}
	}

	/**
	 * 非数组或ArrayList分析入口
	 * 
	 * @param c
	 * @param name
	 * @param o
	 * @param rowNum
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void analyzeObject(Object o, int callCount) {
		if (o != null) {
			// 多维数组之类的东西递归吧递归吧
			if(o.getClass().isArray() || o.getClass().isAssignableFrom(List.class)||o.getClass().isAssignableFrom(ArrayList.class)){
				labelList.add(new Label(callCount, rowNum++, "subArray"));
				analyzeListObject("subArray", o, callCount+1);
			}else{
				// 分析它的每个field
				Field[] fields = o.getClass().getDeclaredFields();
				try {
					for (Field f : fields) {
						Class type = f.getType();
						// 使其可以访问私有field
						f.setAccessible(true);
						if (f.get(o) == null) {
							// labelList.add(new Label(1, rowNum++, f.getName()
							// + " is null."));
						} else if (type.isPrimitive()) {
							// 基本数据类型,不递归
							labelList.add(new Label(callCount, rowNum++, f.getName()
									+ " is:" + f.get(o)));
						} else if (type.isArray()
								|| type.isAssignableFrom(List.class)
								|| type.isAssignableFrom(ArrayList.class)) {
							// 递归处理数组和List
							labelList.add(new Label(callCount, rowNum++, f.getName()+"(Array)"));
							analyzeListObject(f.getName(), f.get(o), callCount+1);
						} else if (type
								.isAssignableFrom(com.sun.corba.se.impl.corba.AnyImpl.class)) {
							// corba any类型
							try {
								analyseAny(f.getName(), (Any) f.get(o),
										callCount);
							} catch (Exception e) {
								
								ExceptionHandler.handleException(e);
							}
						} else if (isIdlEnum(f.get(o))) {
							// IDL枚举，特殊处理，否则会导致死循环
							analyseIdlEnum(f.getName(), f.get(o),callCount);
						} else if (type.isAssignableFrom(String.class)) {
							// String 不递归
							labelList.add(new Label(callCount, rowNum++, f.getName()
									+ " is:" + Stringformat(f.get(o).toString())));
						} else if (type.isAssignableFrom(Integer.class)
								|| type.isAssignableFrom(Double.class)
								|| type.isAssignableFrom(Float.class)
								|| type.isAssignableFrom(Byte.class)
								|| type.isAssignableFrom(Short.class)
								|| type.isAssignableFrom(Long.class)
								|| type.isAssignableFrom(Boolean.class)) {
							// 包装类,不递归
							labelList.add(new Label(callCount, rowNum++, f.getName()
									+ " is:" + Stringformat(f.get(o).toString())));
						} else if (type
								.isAssignableFrom(globaldefs.NameAndStringValue_T.class)) {
							// NameAndStringValue,递归处理
							labelList.add(new Label(callCount, rowNum++, f.getName()
									+ " is a Class:"));
							analyzeObject(f.get(o), callCount+1);

						} else {
							// 余下的统统递归
							labelList.add(new Label(callCount, rowNum++, f.getName()
									+ " is a Class:"));
							analyzeObject(f.get(o), callCount+1);
						}
						// 想到而没添加的：map、set、java枚举、java超大数据类型 ...
					}
				} catch (IllegalArgumentException e) {
					ExceptionHandler.handleException(e);
				} catch (IllegalAccessException e) {
					ExceptionHandler.handleException(e);
				}
			}
			
			
		}
	}

	/**
	 * 处理基本数据类型数组
	 * 
	 * @param o
	 */
	private void analyseArray(String name, Object o, int callCount) {
		name+="("+o.getClass().getSimpleName()+")";
		if (o.getClass().getComponentType().isAssignableFrom(short.class)) {
			short[] shortList = (short[]) o;
			labelList.add(new Label(callCount, rowNum++, name + " is: "
					+ Arrays.toString(shortList)));
		} else if (o.getClass().getComponentType().isAssignableFrom(int.class)) {
			int[] shortList = (int[]) o;
			labelList.add(new Label(callCount, rowNum++, name + " is: "
					+ Arrays.toString(shortList)));
		} else if (o.getClass().getComponentType().isAssignableFrom(long.class)) {
			long[] shortList = (long[]) o;
			labelList.add(new Label(callCount, rowNum++, name + " is: "
					+ Arrays.toString(shortList)));
		} else if (o.getClass().getComponentType().isAssignableFrom(char.class)) {
			char[] shortList = (char[]) o;
			labelList.add(new Label(callCount, rowNum++, name + " is: "
					+ Arrays.toString(shortList)));
		} else if (o.getClass().getComponentType().isAssignableFrom(
				double.class)) {
			double[] shortList = (double[]) o;
			labelList.add(new Label(callCount, rowNum++, name + " is: "
					+ Arrays.toString(shortList)));
		} else if (o.getClass().getComponentType()
				.isAssignableFrom(float.class)) {
			float[] shortList = (float[]) o;
			labelList.add(new Label(callCount, rowNum++, name + " is: "
					+ Arrays.toString(shortList)));
		} else if (o.getClass().getComponentType().isAssignableFrom(
				boolean.class)) {
			boolean[] shortList = (boolean[]) o;
			labelList.add(new Label(callCount, rowNum++, name + " is: "
					+ Arrays.toString(shortList)));
		} else if (o.getClass().getComponentType().isAssignableFrom(byte.class)) {
			byte[] shortList = (byte[]) o;
			labelList.add(new Label(callCount, rowNum++, name + " is: "
					+ Arrays.toString(shortList)));
		}
	}

	/**
	 * 判断是否为IDL枚举类型，防止死循环
	 * 
	 * @param o
	 * @return
	 */
	private boolean isIdlEnum(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (f.getType().isAssignableFrom(o.getClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 处理IDL中的enum类型。。。这货和java enum对应不起来太坑爹了
	 * 
	 * @param o
	 */
	private void analyseIdlEnum(String name, Object o, int callCount) {
		name+="("+o.getClass().getSimpleName()+")";
		// 懒人的临时处理方式好孩子不要学
		// labelList.add(new Label(1, rowNum++, name
		// + " is:" + Stringformat(o.toString())));
		// 稍微不懒人的处理方式,失去了灵活性。。。
		Field[] fields = o.getClass().getDeclaredFields();
		// 正常来说映射出来的enum的第一个field为int类型对象且为值
		try {
			int value = -1;
			String valueString = null;
			// 使其可以访问私有field
			fields[0].setAccessible(true);
			if (fields[0].getType().isAssignableFrom(int.class)) {
				value = fields[0].getInt(o);
			} else {
				// 第一个field不是int，丫肯定不是枚举。。随便打一个然后退了吧。。
				labelList.add(new Label(callCount, rowNum++, name + " is:"
						+ Stringformat(o.toString())));
				return;
			}
			if (value != -1) {
				for (int i = 1; i < fields.length; i++) {
					Field f = fields[i];
					if (f.getType().isAssignableFrom(int.class)) {
						// 使其可以访问私有field
						fields[i].setAccessible(true);
						if (fields[i].getInt(o) == value) {
							// 如果相等就是这个枚举值了
							valueString = fields[i].getName();
							break;
						}
					}
				}
			}
			if (valueString == null) {
				// 没值肯定不是枚举随便打一个
				labelList.add(new Label(callCount, rowNum++, name + " is:"
						+ Stringformat(o.toString())));
			} else {
				// 终于能打枚举值了
				labelList.add(new Label(callCount, rowNum++, name + " is:"
						+ Stringformat(valueString)));
			}

		} catch (IllegalArgumentException e) {
			ExceptionHandler.handleException(e);
		} catch (IllegalAccessException e) {
			ExceptionHandler.handleException(e);
		}

	}
	
	   // Maps TCKind values to names
    // This is also used in AnyImpl.
    static final String[] kindNames = {
        "null",
        "void",
        "short",
        "long",
        "ushort",
        "ulong",
        "float",
        "double",
        "boolean",
        "char",
        "octet",
        "any",
        "typecode",
        "principal",
        "objref",
        "struct",
        "union",
        "enum",
        "string",
        "sequence",
        "array",
        "alias",
        "exception",
        "longlong",
        "ulonglong",
        "longdouble",
        "wchar",
        "wstring",
        "fixed",
        "value",
        "valueBox",
        "native",
        "abstractInterface"
    };
	/**
	 * 分析corba Any 对象
	 * 
	 * @param name
	 * @param o
	 * @param blankString
	 * @throws Exception
	 */
	private void analyseAny(String name, Any any, int callCount)
			throws Exception {
		// Resolve aliases here
		TypeCode realType = realType(any.type());
		name+="("+kindNames[realType.kind().value()]+")";
		switch (realType.kind().value()) {
		// handle primitive types
		case TCKind._tk_null:
		case TCKind._tk_void:
			labelList.add(new Label(callCount, rowNum++, name + " is empty."));
			break;
		case TCKind._tk_short:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_short()));
			break;
		case TCKind._tk_long:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_long()));
			break;
		case TCKind._tk_ushort:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_ushort()));
			break;
		case TCKind._tk_ulong:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_ulong()));
			break;
		case TCKind._tk_float:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_float()));
			break;
		case TCKind._tk_double:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_double()));
			break;
		case TCKind._tk_boolean:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_boolean()));
			break;
		case TCKind._tk_char:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_char()));
			break;
		case TCKind._tk_wchar:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_wchar()));
			break;
		case TCKind._tk_octet:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_octet()));
			break;
		case TCKind._tk_any:
//			labelList.add(new Label(callCount, rowNum++, name + " is :"));
			analyseAny(name, any.extract_any(), callCount);
			break;
		case TCKind._tk_TypeCode:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_TypeCode().toString()));
			break;
		case TCKind._tk_string:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					Stringformat(any.extract_string())));
			break;
		case TCKind._tk_wstring:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					Stringformat(any.extract_wstring())));
			break;
		case TCKind._tk_longlong:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_longlong()));
			break;
		case TCKind._tk_ulonglong:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_ulonglong()));
			break;
		case TCKind._tk_objref:
			// What's this?
			// 如果走到这里了..天知道丫是啥啊囧TZ
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_Object()));
			break;
		case TCKind._tk_Principal:
			// 一个过时的东西,据说是包含关于客户端身份信息的类
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_Object()));
			break;
		case TCKind._tk_enum:
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_long()));
			break;
		case TCKind._tk_fixed:
			// bigDecimal
			labelList.add(new Label(callCount, rowNum++, name + " is :"+
					any.extract_fixed()));
			break;
		case TCKind._tk_except:
		case TCKind._tk_struct:
		case TCKind._tk_union:
		case TCKind._tk_sequence:
		case TCKind._tk_array:
			InputStream copyOfMyStream = any.create_input_stream();
			analyseAnyMember(name, any, realType, copyOfMyStream, callCount+1);
			break;

		// Too complicated to handle value types the way we handle
		// other complex types above. Don't try to decompose it here
		// for faster comparison, just use Object.equals().
		case TCKind._tk_value:
		case TCKind._tk_value_box:
			labelList.add(new Label(callCount, rowNum++, name + " is :"));
			analyzeListObject(name, any.extract_Value(), callCount+1);
			break;
		case TCKind._tk_alias:
			System.out.println("errorResolvingAlias!");
			break;
		case TCKind._tk_longdouble:
			// Unspecified for Java
			System.out.println("tkLongDoubleNotSupported!");
			break;
		default:
			System.out.println("typecodeNotSupported!");
			break;
		}
	}

	// 分析any的成员
	private void analyseAnyMember(String name, Any any, TypeCode memberType,
			InputStream myStream, int callCount) throws Exception {
		// Resolve aliases here
		TypeCode realType = realType(memberType);
		name+="("+kindNames[realType.kind().value()]+")";
		try {
			switch (realType.kind().value()) {
			// handle primitive types
			case TCKind._tk_null:
			case TCKind._tk_void:
				labelList.add(new Label(callCount, rowNum++, name + " is empty."));
				break;
			case TCKind._tk_short:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_short()));
				break;
			case TCKind._tk_long:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_long()));
				break;
			case TCKind._tk_ushort:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_ushort()));
				break;
			case TCKind._tk_ulong:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_ulong()));
				break;
			case TCKind._tk_float:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_float()));
				break;
			case TCKind._tk_double:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_double()));
				break;
			case TCKind._tk_boolean:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_boolean()));
				break;
			case TCKind._tk_char:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_char()));
				break;
			case TCKind._tk_wchar:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_wchar()));
				break;
			case TCKind._tk_octet:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_octet()));
				break;
			case TCKind._tk_any:
//				labelList.add(new Label(callCount, rowNum++, name + " is :"));
				analyseAny(name, myStream.read_any(), callCount);
				break;
			case TCKind._tk_TypeCode:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_TypeCode().toString()));
				break;
			case TCKind._tk_string:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						Stringformat(myStream.read_string())));
				break;
			case TCKind._tk_wstring:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						Stringformat(myStream.read_wstring())));
				break;
			case TCKind._tk_longlong:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_longlong()));
				break;
			case TCKind._tk_ulonglong:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_ulonglong()));
				break;
			case TCKind._tk_objref:
				// What's this?
				// 如果走到这里了..天知道丫是啥啊囧TZ
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_Object()));
				break;
			case TCKind._tk_Principal:
				// 一个过时的东西,据说是包含关于客户端身份信息的类
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_Principal()));
				break;
			case TCKind._tk_enum:
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_long()));
				break;
			case TCKind._tk_fixed:
				// bigDecimal
				labelList.add(new Label(callCount, rowNum++, name + " is :"+
						myStream.read_fixed()));
				break;
			case TCKind._tk_except:
			case TCKind._tk_struct: {
				labelList.add(new Label(callCount, rowNum++, name + " is :"));
				int length = realType.member_count();
				for (int i = 0; i < length; i++) {
					// 分析每个Field
					analyseAnyMember(realType.member_name(i), any,
							realType.member_type(i), myStream, callCount+1);
				}
				break;
			}
			case TCKind._tk_union: {

				try {
					// for orb
					Field field;
					field = any.getClass().getDeclaredField("orb");
					field.setAccessible(true);
					com.sun.corba.se.spi.orb.ORB orb = (com.sun.corba.se.spi.orb.ORB) field
							.get(any);

					labelList.add(new Label(callCount, rowNum++, name + " is :"));
					Any myDiscriminator = orb.create_any();
					myDiscriminator.read_value(myStream,
							realType.discriminator_type());

					TypeCodeImpl realTypeCodeImpl = convertToNative(orb,
							realType);

					// for an unreachable method
					Class[] args = new Class[1];
					args[0] = myDiscriminator.getClass();
					Method m = realTypeCodeImpl.getClass().getDeclaredMethod(
							"currentUnionMemberIndex", args[0]);
					m.setAccessible(true);
					Object[] arguments = new Object[1];
					arguments[0] = myDiscriminator;
					int memberIndex = (Integer) m.invoke(realTypeCodeImpl,
							arguments);

					if (memberIndex == -1)
						throw new Exception("wrapper.unionDiscriminatorError()");

					analyseAnyMember(name, any,
							realType.member_type(memberIndex), myStream,
							callCount+1);
				} catch (SecurityException e) {
					
					ExceptionHandler.handleException(e);
				} catch (NoSuchFieldException e) {
					
					ExceptionHandler.handleException(e);
				} catch (NoSuchMethodException e) {
					
					ExceptionHandler.handleException(e);
				} catch (IllegalArgumentException e) {
					
					ExceptionHandler.handleException(e);
				} catch (IllegalAccessException e) {
					
					ExceptionHandler.handleException(e);
				} catch (InvocationTargetException e) {
					
					ExceptionHandler.handleException(e);
				}
				break;
			}
			case TCKind._tk_sequence: {
				labelList.add(new Label(callCount, rowNum++, name + " is :"));
				int length = myStream.read_long();
				for (int i = 0; i < length; i++) {
					analyseAnyMember(name + "[" + i + "]", any,
							realType.content_type(), myStream, callCount+1);
				}
				break;
			}
			case TCKind._tk_array: {
				labelList.add(new Label(callCount, rowNum++, name + " is :"));
				int length = realType.member_count();

				for (int i = 0; i < length; i++) {
					analyseAnyMember(name + "[" + i + "]", any,
							realType.content_type(), myStream, callCount+1);
				}
				break;
			}

			// Too complicated to handle value types the way we handle
			// other complex types above. Don't try to decompose it here
			// for faster comparison, just use Object.equals().
			case TCKind._tk_value:
			case TCKind._tk_value_box:
				labelList.add(new Label(callCount, rowNum++, name + " is :"));
				org.omg.CORBA_2_3.portable.InputStream mine = (org.omg.CORBA_2_3.portable.InputStream) myStream;
				Object o = mine.read_value();
				analyzeListObject(name, o, callCount+1);
				break;
			case TCKind._tk_alias:
				// error resolving alias above
				System.out.println("errorResolvingAlias!");
				break;
			case TCKind._tk_longdouble:
				System.out.println("tkLongDoubleNotSupported!");
				break;
			default:
				System.out.println("typecodeNotSupported!");
				break;
			}
		} catch (BadKind badKind) { // impossible
			badKind.printStackTrace();
		} catch (Bounds bounds) { // impossible
			bounds.printStackTrace();
		}
	}

	private TypeCode realType(TypeCode aType) {
		TypeCode realType = aType;
		try {
			// Note: Indirect types are handled in kind() method
			while (realType.kind().value() == TCKind._tk_alias) {
				realType = realType.content_type();
			}
		} catch (BadKind bad) { // impossible
			bad.printStackTrace();
		}
		return realType;
	}

	// an unreachable static method
	private static TypeCodeImpl convertToNative(ORB orb, TypeCode tc) {
		if (tc instanceof TypeCodeImpl)
			return (TypeCodeImpl) tc;
		else
			return new TypeCodeImpl(orb, tc);
	}
	
}
