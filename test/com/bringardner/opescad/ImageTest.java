package com.bringardner.opescad;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bringardner.polygon.PolygonFrame;

class ImageTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testCount() {
		boolean [][] img = {
				{true,true,true},
				{true,true,true},
				{true,true,true}
		};
		int expect[] = {3,5,2,5,8,4,3,5,3};
		int pos = 0;
		for(int y=0; y < 3; y++ ) {
			for(int x=0; x < 3; x++ ) {
				int cnt = PolygonFrame.countNeighbors(img, x, y);
				System.out.print(cnt+",");
				assertTrue(cnt == expect[pos++]);
			}
		}
		System.out.println();
	}
	
	@Test
	void testCount2() {
	
		boolean [][] img2 = {
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
		};

		int expect[] = {
				3,5,5,5,5,5,3,
				5,8,8,8,8,8,5,
				5,8,8,8,8,8,5,
				5,8,8,8,8,8,5,
				5,8,8,8,8,8,5,
				5,8,8,8,8,8,5,
				5,8,8,8,8,8,5,
				5,8,8,8,8,8,5,
				3,5,5,5,5,5,3,
			};
		int pos = 0;
		
		for(int y=0; y < img2.length; y++ ) {
			for(int x=0; x < img2[y].length; x++ ) {
				int cnt = PolygonFrame.countNeighbors(img2, x, y);
				System.out.print(cnt+",");
				assertTrue(cnt == expect[pos++]);
			}
			System.out.println();		
		}
		

	}

	@Test
	void testCount3() {
	
		boolean [][] img2 = {
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,false,true,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,true,false,true,true,true,true},
				{true,true,true,true,true,true,true},
				{true,true,true,true,false,true,true},
				{true,true,true,true,true,true,true},
				{true,true,true,true,true,true,true},
		};

		int expect[] = {
				3,5,5,5,5,5,3,
				4,7,7,8,8,8,5,
				4,8,7,8,8,8,5,
				4,6,6,7,8,8,5,
				5,7,8,7,8,8,5,
				5,7,7,6,7,7,5,
				5,8,8,7,8,7,5,
				5,8,8,7,7,7,5,
				3,5,5,5,5,5,3

			};
		int pos = 0;
		
		for(int y=0; y < img2.length; y++ ) {
			for(int x=0; x < img2[y].length; x++ ) {
				int cnt = PolygonFrame.countNeighbors(img2, x, y);
				System.out.print(cnt+",");
				assertTrue(cnt == expect[pos++]);
			}
			System.out.println();		
		}
		

	}

}
