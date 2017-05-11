package com.mock.wificlient;

import io.netty.buffer.ByteBuf;

/**
 * 数据读取
 * @author mock
 *
 */
public class NumberUtil {
	
	/**
	 * 获取某个byte上的第index位bit,注意index从0开始
	 * @param data
	 * @param index
	 * @return
	 */
	public static byte indexOfBit(byte data,int index) {
		return (byte) (data >> index & 1);
	}
	

	/**
	 * 读取Integer值(大端模式)
	 * @param data 数据源
	 * @param index	开始读取的字节位置
	 * @param length 读取的字节长度
	 * @return
	 */
	public static Integer readAsInteger(ByteBuf data,int index,int length) {
		if (length > 4 || length <= 0) throw new RuntimeException("illegal length to read a Integer value!");
		if (length != 4) return readWithRandomLength(data, index, length).intValue();
		Integer result = 0;
		int offset = length;
		for (int i = index;i< (index + length);i++) {
			result += (data.getByte(i) & 0xff) << (8*(--offset));
			System.out.println(result);
		}
		return result;
	}
	
	
	/**
	 * 读取Long值(大端模式)
	 * @param data 数据源
	 * @param index	开始读取的字节位置
	 * @param length 读取的字节长度
	 * @return
	 */
	public static Long readAsLong(ByteBuf data,int index,int length) {
		
		if (length > 8 || length <= 0) throw new RuntimeException("illegal length to read a Long value!");
		if (length != 8) return readWithRandomLength(data, index, length);
		Long result = 0L;
		int offset = length;
		for (int i = index;i< (index + length);i++) {
			result += (data.getByte(i) & 0xff) << (8*(--offset));
		}
		return result;
	}	
	
	/**
	 * 读取Long值(大端模式)
	 * @param data 数据源
	 * @param index	开始读取的字节位置
	 * @param length 读取的字节长度
	 * @return
	 */
	public static Long readWithRandomLength(ByteBuf data,int index,int length) {
		
		if (length > 8 || length <= 0) throw new RuntimeException("illegal length to read a Long value!");
		Long result = 0L;
		int offset = length;
		boolean navigative = false;
		for (int i = index;i< (index + length);i++) {
			int moveBit = 8 *(--offset);
			byte val = data.getByte(i);
			if (val < 0 && i == index) navigative = true;
			//如果整体是负数，则取反
			if (navigative) {
				val = (byte) ~val;
				//取反后有可能此byte还是负数，则转为无符号
				if (val < 0) {
					result += (val & 0xff) << moveBit;
					continue;
				}
			} else if (val < 0) {
				result += (val & 0xff) << moveBit;
				continue;
			}
			result += val << moveBit;
		}
		
		//记得取反后要加上1
		return navigative ? -(result + 1) : result;
	}
	
	
	/**
	 * 读取Long值(小端模式)
	 * @param data 数据源
	 * @param index	开始读取的字节位置
	 * @param length 读取的字节长度
	 * @return
	 */
	public static Long readAsLongWithLittleEndian(ByteBuf data,int index,int length) {
		
		if (length > 8 || length <= 0) throw new RuntimeException("illegal length to read a Long value!");
		Long result = 0L;
		int offset = length;
		boolean navigative = false;
		int begin = index + length - 1;
		for (int i = begin;i< index;i--) {
			int moveBit = 8 *(--offset);
			byte val = data.getByte(i);
			if (val < 0 && i == index) navigative = true;
			//如果整体是负数，则取反
			if (navigative) {
				val = (byte) ~val;
				//取反后有可能此byte还是负数，则转为无符号
				if (val < 0) {
					result += (val & 0xff) << moveBit;
					continue;
				}
			} else if (val < 0) {
				result += (val & 0xff) << moveBit;
				continue;
			}
			result += val << moveBit;
		}
		
		//记得取反后要加上1
		return navigative ? -(result + 1) : result;
	}
}
