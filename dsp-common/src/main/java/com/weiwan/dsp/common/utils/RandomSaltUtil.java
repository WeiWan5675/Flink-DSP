/*
 *      Copyright [2020] [xiaozhennan1995@gmail.com]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *      http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weiwan.dsp.common.utils;

import java.util.Random;

public class RandomSaltUtil {
	
	public static String generateRandomSaltUtil(int n){
		char[] str="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
		Random random = new Random();
		//System.out.println(random.nextInt(str.length));
		String salt="";
		for(int i=0;i<n;i++){
			salt+=str[random.nextInt(str.length)];
		}
			return salt;
	}
	public static void main(String[] args){
		String str=generateRandomSaltUtil(16);
		System.out.println(str);
	}
}
