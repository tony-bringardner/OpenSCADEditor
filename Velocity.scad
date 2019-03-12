//  Velocoty macro to create a rainbow sin wave
#macro(rainbow $colors) 
#set($row=0)

#foreach( $color in $colors )
    for(col=[0:36]) {
    		color("$color")
    		translate( [$row, col, 0] )
		cube( size = [1, 1, 11+10*cos(10*$row)*sin(10*col)] );
    }
#set($row=$row+1)
#end
#end
#rainbow(['Lavender','Thistle','Plum','Violet','Orchid','Fuchsia','Magenta','MediumOrchid','MediumPurple','BlueViolet','DarkViolet','DarkOrchid','DarkMagenta','Purple','Indigo',	'DarkSlateBlue','SlateBlue','MediumSlateBlue','Pink','LightPink','HotPink','DeepPink',	'MediumVioletRed','PaleVioletRed','Aqua','Cyan','LightCyan','PaleTurquoise',	'Aquamarine','Turquoise','MediumTurquoise','DarkTurquoise','CadetBlue','SteelBlue',	'LightSteelBlue','PowderBlue','LightBlue','SkyBlue','LightSkyBlue','DeepSkyBlue','DodgerBlue',	'CornflowerBlue','RoyalBlue','Blue','MediumBlue','DarkBlue','Navy','MidnightBlue',	'IndianRed','LightCoral','Salmon','DarkSalmon','LightSalmon','Red','Crimson','FireBrick',	'DarkRed','GreenYellow','Chartreuse','LawnGreen','Lime','LimeGreen','PaleGreen',	'LightGreen','MediumSpringGreen','SpringGreen','MediumSeaGreen','SeaGreen','ForestGreen',	'Green','DarkGreen','YellowGreen','OliveDrab','Olive','DarkOliveGreen','MediumAquamarine',	'DarkSeaGreen','LightSeaGreen','DarkCyan','Teal','LightSalmon','Coral','Tomato',	'OrangeRed','DarkOrange','Orange','Gold','Yellow','LightYellow','LemonChiffon','LightGoldenrodYellow','PapayaWhip','Moccasin','PeachPuff','PaleGoldenrod','Khaki','DarkKhaki','Cornsilk','BlanchedAlmond','Bisque','NavajoWhite','Wheat','BurlyWood','Tan','RosyBrown','SandyBrown','Goldenrod','DarkGoldenrod','Peru','Chocolate','SaddleBrown','Sienna','Brown','Maroon','White','Snow','Honeydew','MintCream','Azure','AliceBlue','GhostWhite','WhiteSmoke','Seashell','Beige','OldLace','FloralWhite','Ivory','AntiqueWhite','Linen','LavenderBlush','MistyRose','Gainsboro','LightGrey','Silver','DarkGray','Gray','DimGray','LightSlateGray','SlateGray','DarkSlateGray','Black']) 





