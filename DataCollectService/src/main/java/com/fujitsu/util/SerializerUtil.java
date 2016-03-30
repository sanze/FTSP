package com.fujitsu.util;

//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileWriter;
//import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.omg.CORBA.Any;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;

import com.fujitsu.handler.ExceptionHandler;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.spi.orb.ORB;

public class SerializerUtil {
//	public static final String FILEPATH = System.getProperty("user.dir")+"/OutPutFiles/";
//	public static final String seprater = "**********************************seprate**************************************";
	
	public final static String TabSeprater="  ";
	public final static String LineSeprater="\r\n";
	public final static String NULL="null";
	public final static String ObjectStart="{";
	public final static String ObjectEnd="}";
	public final static String ArrayStart="[";
	public final static String ArrayEnd="]";
	public final static String NameValueSeprater=":";
	public final static String AttributeSeprater=",";
	
	private StringBuffer printString;
	private String encode;
	private boolean format;
	private String tabSeprater;
	private String lineSeprater;
	
	public SerializerUtil(){
		this(null,true);
	}
	public SerializerUtil(String encode){
		this(encode,true);
	}
	public SerializerUtil(boolean format){
		this(null,format);
	}
	public SerializerUtil(String encode,boolean format){
		this.encode=encode;
		this.format=format;
		if(this.format){
			tabSeprater=TabSeprater;
			lineSeprater=LineSeprater;
		}else{
			tabSeprater="";
			lineSeprater="";
		}
		this.printString=new StringBuffer();
	}

	public static String toJSON(Object o) {
		return toJSON(null,o,null,true);
	}
	public static String toJSON(Object o,String encode) {
		return toJSON(null,o,encode,true);
	}
	public static String toJSON(Object o,boolean format) {
		return toJSON(null,o,null,format);
	}
	public static String toJSON(Object o,String encode,boolean format) {
		return toJSON(null,o,encode,format);
	}

	/**
	 * 利用反射打印传入的任何对象
	 * 
	 * @param name
	 *            对象名称
	 * @param o
	 *            需要打印的对象
	 * @return
	 */
	public static String toJSON(String name, Object o, 
			String encode, boolean format) {
		try {
			SerializerUtil ser=new SerializerUtil(encode,format);
			// 利用反射分析打印
			ser.analyzeListObject(name, o, "");

			// System.out.println(printString);
			String s = ser.printString.toString();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*public void writeLogFile(String ip, String str){
		
		String path = FILEPATH;
		String targetFileName = ip;
		try {
			File file = new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
			FileWriter targetFile = new FileWriter(path + targetFileName+".txt",true);
			targetFile.write(str);
			targetFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeLogFile(String dir, String fileName, String str, boolean append){
		
		String path = FILEPATH
				+ dir + "/";
		String targetFileName = fileName;
		try {
			File file = new File(path);
			if(!file.exists()){
				file.mkdirs();
			}
			FileWriter targetFile = new FileWriter(path + targetFileName+".txt",append);
			targetFile.write(str);
			targetFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	/**
	 * 数组和ArrayList分析入口
	 * 
	 * @param o
	 * @param rowNum
	 */
	private void analyzeListObject(String name, Object o, String blankString) {
		String nameM=(name==null||name.isEmpty())?"":name+NameValueSeprater;
		if (o != null) {
			if (o.getClass().isArray()) {
				// 若为数组
				if (o.getClass().getComponentType().isPrimitive()) {
					// 如果是基本数据类型数组那么它就不能被转换为Object[]好烦啊
					analyseArray(name, o, blankString + tabSeprater);
				} else {
					Object[] datas = (Object[]) o;
					printString.append(blankString + nameM + ArrayStart + lineSeprater);
					for (int i = 0; i < datas.length; i++) {
						// 分析Array中每个对象,假定可能是list
						analyzeListObject(null, datas[i],
								blankString + tabSeprater);
						if(i!=datas.length-1)
							printString.append(AttributeSeprater);
						printString.append(lineSeprater);
					}
					printString.append(blankString + ArrayEnd);
				}
			} else if (o.getClass().isAssignableFrom(List.class)
					|| o.getClass().isAssignableFrom(ArrayList.class)) {
				printString.append(blankString + nameM + ArrayStart + lineSeprater);
				// 若为List
				List datas = (List) o;
				// 目测IDL包里面没用到基本数据类型的ArrayList,暂时先不管他
				for (int i = 0; i < datas.size(); i++) {
					// 分析ArrayList中每个对象,假定可能是list
					analyzeListObject(null, datas.get(i),
							blankString + tabSeprater);
					if(i!=datas.size()-1)
						printString.append(AttributeSeprater);
					printString.append(lineSeprater);
				}
				printString.append(blankString + ArrayEnd);
			} else {
				// 若非数组对象
				analyzeObject(name, o, blankString);
			}
		} else {
			// 数据为空
			printString.append(blankString + nameM + NULL);
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
	private void analyzeObject(String name, Object o, String blankString) {
		String nameM=(name==null||name.isEmpty())?"":name+NameValueSeprater;
		if (o != null) {
			Class type = o.getClass();
			if (type.isAssignableFrom(com.sun.corba.se.impl.corba.AnyImpl.class)) {
				// corba any类型
				try {
					analyseAny(name, (Any) o, blankString);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (isIdlEnum(o)) {
				// IDL枚举，特殊处理，否则会导致死循环
				analyseIdlEnum(name, o, blankString);
			} else if (type.isPrimitive()) {
				// 基本数据类型,不递归
				printString.append(blankString + nameM + o);
			} else if (type.isAssignableFrom(String.class)) {
				// String 不递归
				printString.append(blankString + nameM
						+ Stringformat(o.toString()));
			} else if (type.isAssignableFrom(Integer.class)
					|| type.isAssignableFrom(Double.class)
					|| type.isAssignableFrom(Float.class)
					|| type.isAssignableFrom(Byte.class)
					|| type.isAssignableFrom(Short.class)
					|| type.isAssignableFrom(Long.class)
					|| type.isAssignableFrom(Boolean.class)) {
				// 包装类,不递归
				printString.append(blankString + nameM + o);
			} else {
				printString.append(blankString + nameM + ObjectStart + lineSeprater);
				// 类,获取并分析它的每个field
				Field[] fields = o.getClass().getDeclaredFields();
				try {
					for (int i=0;i<fields.length;i++) {
						Field f=fields[i];
						String childBlankString=blankString+tabSeprater;
						type = f.getType();
						// 使其可以访问私有field
						f.setAccessible(true);
						if (f.get(o) == null) {
							// 空field
							printString.append(childBlankString + f.getName() + NameValueSeprater
									+ NULL);
						} else if (type.isArray()
								|| type.isAssignableFrom(List.class)
								|| type.isAssignableFrom(ArrayList.class)) {
							// 递归处理数组和List
							analyzeListObject(f.getName(), f.get(o),
									childBlankString);
						} else if (type
								.isAssignableFrom(com.sun.corba.se.impl.corba.AnyImpl.class)) {
							// corba any类型，截止目前仍为需要人类来手动分辨的苦逼类型
							try {
								analyseAny(f.getName(), (Any) f.get(o),
										childBlankString);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else if (isIdlEnum(f.get(o))) {
							// IDL枚举，特殊处理，否则会导致死循环
							analyseIdlEnum(f.getName(), f.get(o), blankString);
						} else if (type.isPrimitive()) {
							// 基本数据类型,不递归
							printString.append(childBlankString + f.getName()
									+ NameValueSeprater + f.get(o));
						} else if (type.isAssignableFrom(String.class)) {
							// String 不递归
							printString.append(childBlankString + f.getName()
									+ NameValueSeprater
									+ Stringformat(f.get(o).toString()));
						} else if (type.isAssignableFrom(Integer.class)
								|| type.isAssignableFrom(Double.class)
								|| type.isAssignableFrom(Float.class)
								|| type.isAssignableFrom(Byte.class)
								|| type.isAssignableFrom(Short.class)
								|| type.isAssignableFrom(Long.class)
								|| type.isAssignableFrom(Boolean.class)) {
							// 包装类,不递归
							printString.append(childBlankString + f.getName()
									+ NameValueSeprater
									+ f.get(o));
						} else {
							// 递归处理非空且非数组field
							analyzeListObject(f.getName(), f.get(o),
									childBlankString);
						}
						if(i!=fields.length-1)
							printString.append(AttributeSeprater);
						printString.append(lineSeprater);
						// 想到而没添加的：map、set、java枚举、java超大数据类型 ...
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				printString.append(blankString + ObjectEnd);
			}
		}
	}

	/**
	 * 处理基本数据类型数组
	 * 
	 * @param o
	 */
	private void analyseArray(String name, Object o, String blankString) {
		String nameM=(name==null||name.isEmpty())?"":name+NameValueSeprater;
		if (o.getClass().getComponentType().isAssignableFrom(short.class)) {
			short[] shortList = (short[]) o;
			printString.append(blankString + nameM
					+ Arrays.toString(shortList));
		} else if (o.getClass().getComponentType().isAssignableFrom(int.class)) {
			int[] shortList = (int[]) o;
			printString.append(blankString + nameM
					+ Arrays.toString(shortList));
		} else if (o.getClass().getComponentType().isAssignableFrom(long.class)) {
			long[] shortList = (long[]) o;
			printString.append(blankString + nameM
					+ Arrays.toString(shortList));
		} else if (o.getClass().getComponentType().isAssignableFrom(char.class)) {
			char[] shortList = (char[]) o;
			printString.append(blankString + nameM
					+ Arrays.toString(shortList));
		} else if (o.getClass().getComponentType()
				.isAssignableFrom(double.class)) {
			double[] shortList = (double[]) o;
			printString.append(blankString + nameM
					+ Arrays.toString(shortList));
		} else if (o.getClass().getComponentType()
				.isAssignableFrom(float.class)) {
			float[] shortList = (float[]) o;
			printString.append(blankString + nameM
					+ Arrays.toString(shortList));
		} else if (o.getClass().getComponentType()
				.isAssignableFrom(boolean.class)) {
			boolean[] shortList = (boolean[]) o;
			printString.append(blankString + nameM
					+ Arrays.toString(shortList));
		} else if (o.getClass().getComponentType().isAssignableFrom(byte.class)) {
			byte[] shortList = (byte[]) o;
			printString.append(blankString + nameM
					+ Arrays.toString(shortList));
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
	private void analyseIdlEnum(String name, Object o, String blankString) {
		String nameM=(name==null||name.isEmpty())?"":name+NameValueSeprater;
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
				printString.append(blankString + nameM
						+ Stringformat(o.toString()));
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
				printString.append(blankString + nameM
						+ Stringformat(o.toString()));
			} else {
				// 终于能打枚举值了
				printString.append(blankString + nameM
						+ Stringformat(valueString));
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 分析corba Any 对象
	 * 
	 * @param name
	 * @param o
	 * @param blankString
	 * @throws Exception
	 */
	private void analyseAny(String name, Any any, String blankString)
			throws Exception {
		String nameM=(name==null||name.isEmpty())?"":name+NameValueSeprater;
		// Resolve aliases here
		TypeCode realType = realType(any.type());
		switch (realType.kind().value()) {
		// handle primitive types
		case TCKind._tk_null:
		case TCKind._tk_void:
			printString.append(blankString + nameM + NULL);
			break;
		case TCKind._tk_short:
			printString.append(blankString + nameM
					+ any.extract_short());
			break;
		case TCKind._tk_long:
			printString.append(blankString + nameM
					+ any.extract_long());
			break;
		case TCKind._tk_ushort:
			printString.append(blankString + nameM
					+ any.extract_ushort());
			break;
		case TCKind._tk_ulong:
			printString.append(blankString + nameM
					+ any.extract_ulong());
			break;
		case TCKind._tk_float:
			printString.append(blankString + nameM
					+ any.extract_float());
			break;
		case TCKind._tk_double:
			printString.append(blankString + nameM
					+ any.extract_double());
			break;
		case TCKind._tk_boolean:
			printString.append(blankString + nameM
					+ any.extract_boolean());
			break;
		case TCKind._tk_char:
			printString.append(blankString + nameM
					+ any.extract_char());
			break;
		case TCKind._tk_wchar:
			printString.append(blankString + nameM
					+ any.extract_wchar());
			break;
		case TCKind._tk_octet:
			printString.append(blankString + nameM
					+ any.extract_octet());
			break;
		case TCKind._tk_any:
//			printString.append(blankString + name + " contains an Any.");
			analyseAny(name, any.extract_any(), blankString + tabSeprater);
			break;
		case TCKind._tk_TypeCode:
//			printString.append(blankString + name
//					+ " contains a TypeCode and it is :");
			printString.append(blankString + tabSeprater + nameM
					+ any.extract_TypeCode().toString());
			break;
		case TCKind._tk_string:
			printString.append(blankString + nameM
					+ Stringformat(any.extract_string()));
			break;
		case TCKind._tk_wstring:
			printString.append(blankString + nameM
					+ Stringformat(any.extract_wstring()));
			break;
		case TCKind._tk_longlong:
			printString.append(blankString + nameM
					+ any.extract_longlong());
			break;
		case TCKind._tk_ulonglong:
			printString.append(blankString + nameM
					+ any.extract_ulonglong());
			break;
		case TCKind._tk_objref:
			// What's this?
			// 如果走到这里了..天知道丫是啥啊囧TZ
			printString.append(blankString + nameM
					+ any.extract_Object());
			break;
		case TCKind._tk_Principal:
			// 一个过时的东西,据说是包含关于客户端身份信息的类
			printString.append(blankString + nameM
					+ any.extract_Object());
			break;
		case TCKind._tk_enum:
			printString.append(blankString + nameM
					+ any.extract_long());
			break;
		case TCKind._tk_fixed:
			// bigDecimal
			printString.append(blankString + nameM
					+ any.extract_fixed());
			break;
		case TCKind._tk_except:
		case TCKind._tk_struct:
		case TCKind._tk_union:
		case TCKind._tk_sequence:
		case TCKind._tk_array:
			InputStream copyOfMyStream = any.create_input_stream();
			analyseAnyMember(name, any, realType, copyOfMyStream, blankString);
			break;

		// Too complicated to handle value types the way we handle
		// other complex types above. Don't try to decompose it here
		// for faster comparison, just use Object.equals().
		case TCKind._tk_value:
		case TCKind._tk_value_box:
//			printString.append(blankString + name + " contains an Object."
//					+ lineSeprater);
			analyzeListObject(name, any.extract_Value(), blankString + tabSeprater);
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
			InputStream myStream, String blankString) throws Exception {
		String nameM=(name==null||name.isEmpty())?"":name+NameValueSeprater;
		// Resolve aliases here
		TypeCode realType = realType(memberType);

		try {
			switch (realType.kind().value()) {
			// handle primitive types
			case TCKind._tk_null:
			case TCKind._tk_void:
				printString.append(blankString + nameM + NULL);
				break;
			case TCKind._tk_short:
				printString.append(blankString + nameM
						+ myStream.read_short());
				break;
			case TCKind._tk_long:
				printString.append(blankString + nameM
						+ myStream.read_long());
				break;
			case TCKind._tk_ushort:
				printString.append(blankString + nameM
						+ myStream.read_ushort());
				break;
			case TCKind._tk_ulong:
				printString.append(blankString + nameM
						+ myStream.read_ulong());
				break;
			case TCKind._tk_float:
				printString.append(blankString + nameM
						+ myStream.read_float());
				break;
			case TCKind._tk_double:
				printString.append(blankString + nameM
						+ myStream.read_double());
				break;
			case TCKind._tk_boolean:
				printString.append(blankString + nameM
						+ myStream.read_boolean());
				break;
			case TCKind._tk_char:
				printString.append(blankString + nameM
						+ myStream.read_char());
				break;
			case TCKind._tk_wchar:
				printString.append(blankString + nameM
						+ myStream.read_wchar());
				break;
			case TCKind._tk_octet:
				printString.append(blankString + nameM
						+ myStream.read_octet());
				break;
			case TCKind._tk_any:
//				printString.append(blankString + name + " is a Corba Any.\r\n");
				analyseAny(name, myStream.read_any(), blankString + tabSeprater);
				break;
			case TCKind._tk_TypeCode:
//				printString.append(blankString + name
//						+ " is a TypeCode and it is :");
				printString.append(blankString + tabSeprater+nameM
						+ myStream.read_TypeCode().toString());
				break;
			case TCKind._tk_string:
				printString.append(blankString + nameM
						+ Stringformat(myStream.read_string()));
				break;
			case TCKind._tk_wstring:
				printString.append(blankString + nameM
						+ Stringformat(myStream.read_wstring()));
				break;
			case TCKind._tk_longlong:
				printString.append(blankString + nameM
						+ myStream.read_longlong());
				break;
			case TCKind._tk_ulonglong:
				printString.append(blankString + nameM
						+ myStream.read_ulonglong());
				break;
			case TCKind._tk_objref:
				// What's this?
				// 如果走到这里了..天知道丫是啥啊囧TZ
				printString.append(blankString + nameM
						+ myStream.read_Object());
				break;
			case TCKind._tk_Principal:
				// 一个过时的东西,据说是包含关于客户端身份信息的类
				printString.append(blankString + nameM
						+ myStream.read_Principal()
						+ lineSeprater);
				break;
			case TCKind._tk_enum:
				printString.append(blankString + nameM
						+ myStream.read_long());
				break;
			case TCKind._tk_fixed:
				// bigDecimal
				printString.append(blankString + nameM
						+ myStream.read_fixed());
				break;
			case TCKind._tk_except:
			case TCKind._tk_struct: {
				printString.append(blankString + nameM + ObjectStart+lineSeprater);
				int length = realType.member_count();
				for (int i = 0; i < length; i++) {
					// 分析每个Field
					analyseAnyMember(realType.member_name(i), any,
							realType.member_type(i), myStream, blankString
									+ tabSeprater);
					if(i!=length-1)
						printString.append(AttributeSeprater);
					printString.append(lineSeprater);
				}
				printString.append(blankString + ObjectEnd);
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

//					printString.append(blankString + name + ":\r\n");
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
							blankString + tabSeprater);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			case TCKind._tk_sequence: {
				printString.append(blankString + nameM + ArrayStart+lineSeprater);
				int length = myStream.read_long();
				for (int i = 0; i < length; i++) {
					analyseAnyMember(null, any,
							realType.content_type(), myStream, 
							blankString + tabSeprater);
					if(i!=length-1)
						printString.append(AttributeSeprater);
					printString.append(lineSeprater);
				}
				printString.append(blankString + ArrayEnd);
				break;
			}
			case TCKind._tk_array: {
				printString.append(blankString + nameM + ArrayStart+lineSeprater);
				int length = realType.member_count();

				for (int i = 0; i < length; i++) {
					analyseAnyMember(null, any,
							realType.content_type(), myStream, 
							blankString + tabSeprater);
					if(i!=length-1)
						printString.append(AttributeSeprater);
					printString.append(lineSeprater);
				}
				printString.append(blankString + ArrayEnd);
				break;
			}

			// Too complicated to handle value types the way we handle
			// other complex types above. Don't try to decompose it here
			// for faster comparison, just use Object.equals().
			case TCKind._tk_value:
			case TCKind._tk_value_box:
//				printString.append(blankString + name + " is an Object.\r\n");
				org.omg.CORBA_2_3.portable.InputStream mine = (org.omg.CORBA_2_3.portable.InputStream) myStream;
				Object o = mine.read_value();
				analyzeListObject(name, o, blankString + tabSeprater);
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

	/**
	 * 转码
	 * 
	 * @param value
	 * @return
	 */
	private String Stringformat(String value) {
		String result=null;
		if(value==null) return NULL;
		try {
			result = encode==null?value:new String(value.getBytes("ISO8859_1"), encode);
		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.handleException(e);
		}
		return "\""+result+"\"";
	}
}
