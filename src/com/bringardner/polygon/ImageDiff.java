package com.bringardner.polygon;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageDiff {

	private static boolean merge(Rectangle r1, Rectangle r2, int hpad,int vpad) {
		boolean ret = false;

		int mnx = r1.x-hpad;
		int mxx = r1.x+r1.width+hpad;
		int mny = r1.y-vpad;
		int mxy = r1.y+r1.height+vpad;

		ret = r2.x >= mnx && r2.x <= mxx &&	r2.y >= mny && r2.y <= mxy ;


		if( !ret ) {
			Rectangle r3 = new Rectangle(r1);
			r3.x-=hpad;
			r3.width+=hpad;
			r3.y-=vpad;
			r3.height+=vpad;
			ret = r3.intersects(r2);
		}

		return ret;
	}

	private static void merge(Rectangle r2, List<Rectangle> list, int hgap,int vpad) {

		for (Rectangle r : list) {
			boolean b = r.intersects(r2) || merge(r,r2,hgap,vpad) || merge(r2,r,hgap,vpad); 
			if( b ) {		
				r.add(r2);
				return;
			}

		}

		list.add(r2);
	}

	private static Rectangle find(Rectangle[][] m,int x, int y, int gap) {
		Rectangle ret = null;
		// at 200 dpi transit has 5.2 space between the two segments :-(
		for(int r=0; ret==null && r <= gap;r++ ) {
			ret = lookBehind(r,x,y,m);
		}
		if( ret == null && y > 0 ) {
			try {
				ret = m[y-1][x];	
			} catch (Exception e) {
			}
			
		}


		return ret;
	}


	private static Rectangle lookBehind(int r, int x, int y,	Rectangle[][] m) {
		Rectangle ret = null;
		int tx = x-r;
		if( tx >= 0 && tx < m[0].length) {
			// this array is backwards
			ret = m[y][tx];
		}
		return ret;
	}

	public static List<Rectangle> findEdges(boolean[][] img,Rectangle rect, int hgap,int vgap) {


		System.out.println("r="+rect);
		System.out.println("w="+img.length+" h="+img[0].length);
		
		List<Rectangle> ret = new ArrayList<Rectangle>();
		Rectangle m [][] = new Rectangle[img.length][img[0].length];
		System.out.println("r w="+m.length+" r h="+m[0].length);
		//if( rect.y >= 0 && rect.x >= hgap ) {
		for(int x=rect.x,maxx = rect.x+rect.width; x<maxx; x++ ) {
			for(int y=rect.y,maxy=rect.y+rect.height; y<maxy; y++ ) {
				// include 'gap' pixels to the left of the rectangle, no, no, no :-(
				
					if( y < img.length && x < img[0].length ) {
						try {
							if( img[y][x] ) {

								Rectangle cur = find(m,x,y,hgap);
								if( cur == null ) {
									cur = new Rectangle(x, y, 1, 1);
									ret.add(cur);
								} else {
									cur.add(x,y);
								}
								if( y< m.length && x < m[0].length) {
									m[y][x] = cur;
								}
							}
						} catch(Throwable e) {
							//DebugDialog.println("ImageDiff.findEdges e="+e +" x="+x+" y="+y+" maxx="+img.length+" maxy="+img[0].length);
							e.printStackTrace();
						}
					}
				}
			}
		//}

		//  Add 1 to w + h
		if( ret.size()  > 0 ) {

			for (Rectangle r : ret) {
				r.width++;
				r.height++;
			}

			Collections.sort(ret, new Comparator<Rectangle>() {
				public int compare(Rectangle o1, Rectangle o2) {
					int ret = o1.x - o2.x;
					return ret;
				};
			});


			boolean done = false;
			do {
				Rectangle r = ret.get(0);
				List<Rectangle> list = new ArrayList<Rectangle>();
				list.add(r);
				for(int idx=1,sz1=ret.size(); idx < sz1; idx++ ) {
					Rectangle r2 = ret.get(idx);
					merge(r2,list,hgap,vgap);
				}
				done = list.size() == ret.size();
				ret = list;

			} while(!done);


		}

		Collections.sort(ret, new Comparator<Rectangle>() {

			@Override
			public int compare(Rectangle arg0, Rectangle arg1) {
				return arg0.x - arg1.x;
			}

		});

		/*
		for(int idx=0,sz1=ret.size(); idx < sz1; idx++ ) {
			Rectangle r = ret.get(idx);
			if( ctx.isDebug(r)){
				ImagePanel.print("ImageDiff.findEdges exit idx="+idx, r, ctx.getImg());
			}
		}
		*/

		return ret;
	}

}
