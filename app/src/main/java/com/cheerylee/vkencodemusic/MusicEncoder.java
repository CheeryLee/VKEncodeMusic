/*
 * Copyright (c) 2017 Alexander "CheeryLee" Pluzhnikov
 * 
 * This file is part of VKEncodeMusic.
 * 
 * VKEncodeMusic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * VKEncodeMusic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VKEncodeMusic.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cheerylee.vkencodemusic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MusicEncoder {
	
	String outFilename;
	String inFilename;
	
	int mask[] = { 0x0D, 0x1E, 0x2F, 0x40, 0x51, 0x62, 0x73, 0x84, 0x95,
                       0xA6, 0xB7, 0xC8, 0xD9, 0xEA, 0xFB, 0x0C };
	
	public MusicEncoder(String _in, String _out) {
		inFilename = _in;
		outFilename = _out;
	}
	
	public void processBytes() {
		int str_byte = 0;
		
		File encodedFile = new File(inFilename);
		
		try {
			FileInputStream finStream = new FileInputStream(encodedFile);
			FileOutputStream foutStream = new FileOutputStream(outFilename);
			byte buffer[] = new byte[finStream.available()];
			finStream.read(buffer, 0, finStream.available());
			
			for (int i = 0; i < buffer.length; i++) {
				//int f_byte = buffer[i].intValue();
				
				buffer[i] ^= mask[str_byte];
				mask[str_byte] += 0x10;
				
				if (str_byte < 15)
					str_byte++;
				else
					str_byte = 0;
			}
			
			foutStream.write(buffer, 0, buffer.length);
			finStream.close();
			foutStream.close();
			
			encodedFile.delete();
			
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}
	}
}
