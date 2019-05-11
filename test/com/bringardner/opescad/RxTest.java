package com.bringardner.opescad;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RxTest {
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
		
	}
	

	public static List<Point2D> parsePoints(String code) {
		List<Point2D> ret = new ArrayList<Point2D>();
		if( code.indexOf("paths") >=0 ) {
			
		}
		
		return ret;
	}
	
	@Test
	void test() {
		Pattern p1 = Pattern.compile("points\\s?=\\s?\\[.*\\].*",Pattern.DOTALL| Pattern.MULTILINE);
		
		String rx = "^points\\s?=\\s?\\[.*\\]";
		System.out.println(rx);
		p1 = Pattern.compile(rx,Pattern.DOTALL| Pattern.MULTILINE);
		//p1 = Pattern.compile("(?<pair>\\[[0-9]*[,][0-9]+\\][,]?)*",Pattern.DOTALL| Pattern.MULTILINE);
		
		String code = (
				"points=[[8,17],[0,144],[1,154],[179,10],[176,8],[16,0],[8,1],[193,195],[331,74],[327,72],[199,64],[192,65],[130,175],[16,202],[8,282],[9,291],[131,178],[134,175]]," + 
				"paths= [[0,1,2,3,4,5,6],[7,8,9,10,11],[12,13,14,15,16,17,12]],convexity=10"
				).replace('\r', ' ').replace('\n', ' ').trim()
				;
		
		System.out.println(code);
		Matcher m = p1.matcher(code);
		System.out.println(m.matches());
		System.out.println(m.find());
		System.out.println(m.groupCount());
		
	}

	//@Test
	void test2() {
		Pattern p1 = Pattern.compile("points\\s?=\\s?\\[.*\\].*",Pattern.DOTALL| Pattern.MULTILINE);
		p1 = Pattern.compile("points\\s?=\\s?\\[(?<pair>\\[[0-9]*[,][0-9]+\\][,]?)+]*",Pattern.DOTALL| Pattern.MULTILINE);
		p1 = Pattern.compile("(?<pair>\\[[0-9]*[,][0-9]+\\][,]?)*",Pattern.DOTALL| Pattern.MULTILINE);
		
		String code = "points=" + 
				"[" + 
				"[1,28],[48,28],[49,88],[55,87],[55,28],[82,27],[80,16],[75,10],[68,6],[41,2],[8,0],[0,1]," + 
				"]"
				;
		
		String target = "[1,28],[48,28],[49,88],[55,87],[55,28],[82,27],[80,16],[75,10],[68,6],[41,2],[8,0],[0,1],";
	
		Pattern compileFirst = Pattern.compile("(?<number>[0-9]+)(,([0-9])+)*");
		Pattern compileFollowing = Pattern.compile(",(?<number>[0-9]+)");
	
		Matcher matcherFirst = compileFirst.matcher(target);
		Matcher matcherFollowing = compileFollowing.matcher(target);
	
		System.out.println("matches: " + matcherFirst.matches());
		System.out.println("first: " + matcherFirst.group("number"));
	
		int start = 0;
		while (matcherFollowing.find(start)) {
		    String group = matcherFollowing.group("number");
	
		    System.out.println("following: " + start + " - " + group);
		    start = matcherFollowing.end();
		}
	}

}
