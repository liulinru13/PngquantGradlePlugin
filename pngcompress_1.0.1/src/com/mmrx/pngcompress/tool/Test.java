package com.mmrx.pngcompress.tool;


import java.util.List;

public class Test {
	
	public static void main(String[] args){
		PngCompress compress = new PngCompress("/Users/mmrx/Documents/code/gitHub/PngquantGradlePlugin/pnglib/"
				,new String[]{"/Users/mmrx/Documents/code/idea_workspace/png/"});
		compress.doCompress();
	}

}
