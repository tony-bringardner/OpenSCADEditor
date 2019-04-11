package com.bringardner.openscad;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuration {
	private static File configFile;
	private static Configuration global;
	private int maxRecent;
	private int maxBackup;
	private int minVariableNameLength;
	private String backupFolder;
	private String execPath;
	private List<Template> templates;
	private boolean pacmanEnabled=false;
	
	/**
	 * templates2 is given to other objects so templates can not be modified
	 */
	private transient List<Template> templates2;

	public static Configuration getInstance()  {
		if( global == null ) {
			synchronized (Configuration.class) {
				if( global == null ) {
					Configuration tmp = new Configuration();
					File file = getConfigFile();
					if( file.exists()) {
						try {
							JAXBContext ctx = JAXBContext.newInstance(Configuration.class);
							Unmarshaller um = ctx.createUnmarshaller();
							tmp = (Configuration) um.unmarshal(file);
						} catch (JAXBException e) {
							JOptionPane.showMessageDialog(null, e.getMessage(), "Error manageing configuration", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						tmp = createDefault();
					}
					global = tmp;
				}
			}
		}

		return global;
	}

	private static File getConfigFile() {
		if( configFile == null ) {
			synchronized (Configuration.class) {
				if( configFile == null ) {
					File home = new File(System.getProperty("user.home"));
					File dir = new File(home,".jOpenscap");
					if(!dir.exists()) {
						dir.mkdirs();
					}
					configFile = new File(dir,"Config.xml");
				}
			}
		}

		return configFile;
	}

	public void save () throws IOException {
		try {
			JAXBContext ctx = JAXBContext.newInstance(Configuration.class);
			Marshaller m = ctx.createMarshaller();
			// output pretty printed
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(this, getConfigFile());
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	public int getMaxRecent() {
		return maxRecent;
	}


	public void setMaxRecent(int maxRecent) {
		this.maxRecent = maxRecent;
	}


	public int getMaxBackup() {
		return maxBackup;
	}


	public void setMaxBackup(int maxBackup) {
		this.maxBackup = maxBackup;
	}


	public int getMinVariableNameLength() {
		return minVariableNameLength;
	}


	public void setMinVariableNameLength(int minVariableNameLength) {
		this.minVariableNameLength = minVariableNameLength;
	}


	public String getBackupFolder() {
		return backupFolder;
	}


	public void setBackupFolder(String backupFolder) {
		this.backupFolder = backupFolder;
	}


	public String getExecPath() {
		return execPath;
	}


	public void setExecPath(String execPath) {
		this.execPath = execPath;
	}



	public boolean isPacmanEnabled() {
		return pacmanEnabled;
	}

	public void setPacmanEnabled(boolean pacmanEnabled) {
		this.pacmanEnabled = pacmanEnabled;
	}

	public List<Template> getTemplates() {
		if( templates2 == null && templates != null ) {
			synchronized (this) {
				if( templates2 == null && templates != null ) {
					List<Template> tmp = new ArrayList<>();
					for (Template t : templates) {
						tmp.add(t.copy());
					}
					templates2 = tmp;
				}
			}
		}
		
		return templates2;
	}


	public void setTemplates(List<Template> templates) {
		this.templates = templates;
		templates2 = null;
	}

	private static void populateDefault(List<Template> templates) {


		templates.add(new Template( "linear_extrude", "linear_extrude", "linear_extrude (height = ${height}, center = ${center}, convexity = ${convexity}, twist = ${twist} ) {\n\t${cursor}\n}"));
		templates.add(new Template( "rotate_extrude", "rotate_extrude", "rotate_extrude ( ) {\n\t${cursor}\n}"));
		templates.add(new Template( "for", "for-loop", 
				"for(${idx} = [${start} : ${increment} : ${end}]) {\n\t${cursor}\n}"));

		templates.add(new Template( "module", "module", 
				"module ${"+ModuleAutoCompleteManager.EDIT_MODULE_NAME+"} () {\n\t${cursor}\n}"));
		templates.add(new Template( "function", "function", "function ${name} (${parameters}) = ${value} ${cursor}"));
		templates.add(new Template( "include", "include", "include <${file_name_no_quotes}>; ${cursor}"));
		templates.add(new Template( "use", "use", "use <${file_name_no_quotes}>; ${cursor}"));

		templates.add(new Template( "circle", "circle-radius", "circle(${radius}) ${cursor}"));
		templates.add(new Template( "circle", "circle-diameter", "circle(d=${diameter}) ${cursor}"));

		templates.add(new Template( "square", "square(size,center)", "square(${size},${true} ) ${cursor}"));
		templates.add(new Template( "square", "square([w,h],center)", "square(${width},${height},${true}) ${cursor}"));

		templates.add(new Template( "polygon", "polygon(points)", "polygon(${points} ) ${cursor}"));
		templates.add(new Template( "polygon", "polygon(points,paths)", "polygon(${points},${paths} ) ${cursor}"));

		templates.add(new Template( "text", "text", "text(${text}) ${cursor}"));
		templates.add(new Template( "text", "text(size,font)", "text(text=${text},size=${size},font=${font}) ${cursor}"));
		templates.add(new Template( "text", "text-all", "text(text=${text},size=${size},font=${font},halign=${halign},valign=${valign}) ${cursor}"));

		templates.add(new Template( "sphere", "sphere-radius", "sphere(${radius}) ${cursor}"));
		templates.add(new Template( "sphere", "sphere-diameter", "sphere(d=${diameter}) ${cursor}"));

		templates.add(new Template( "cube", "cube(size,center)", "cube(${size},${center}) ${cursor}"));
		templates.add(new Template( "cube", "cube(w,d,h,center)", "cube([${width},${depth},${height}],center=${center}) ${cursor}"));

		templates.add(new Template( "cylinder", "cylinder", "cylinder(h=${height}, r=${radius}) ${cursor}"));
		templates.add(new Template( "cylinder", "cylinder-radius", "cylinder(h = ${height}, r1 = ${BottomRadius}, r2 = ${TopRadius}, center=${center}) ${cursor}"));
		templates.add(new Template( "cylinder", "cylinder-diameter", "cylinder(h = ${height}, d1 = ${BottomDiameter}, d2 = ${TopDiameter}, center=${center}) ${cursor}"));

		templates.add(new Template( "polyhedron", "polyhedron", "polyhedron(points=${points},faces=${faces}, convexity=${convexity}) ${cursor}"));

		templates.add(new Template("union","union ()"));
		templates.add(new Template("difference","difference ()"));
		templates.add(new Template("intersection","intersection ()"));



		templates.add(new Template( "translate", "translate", "translate([${x},${y},${z}]) ${cursor}"));
		templates.add(new Template( "rotate", "rotate", "rotate([${deg_x},${deg_y},${deg_z}]) ${cursor}"));
		templates.add(new Template( "scale", "scale", "scale([${x},${y},${z}]) ${cursor}"));
		templates.add(new Template( "resize", "resize", "resize([${x},${y},${z}]) ${cursor}"));
		templates.add(new Template( "mirror", "mirror", "mirror([${x},${y},${z}]) ${cursor}"));

		templates.add(new Template( "multmatrix", "multmatrix", "multmatrix(${matrix}) ${cursor}"));

		templates.add(new Template( "color", "color", "color(${name}) ${cursor}"));
		templates.add(new Template( "color", "color-alpha", "color(${name},${alpha}) ${cursor}"));
		templates.add(new Template( "color", "color(r,g,b,a)", "color(${red},${green},${blue},${alpha}) ${cursor}"));

		templates.add(new Template( "offset", "offset-radius", "offset(r=${radius}) ${cursor}"));
		templates.add(new Template( "offset", "offset-delta", "offset(delta=${delta},chamfer=${chamfer}) ${cursor}"));
		templates.add(new Template("hull","hull ()"));
		templates.add(new Template("minkowski","minkowski ()"));

		templates.add(new Template( "true", "true"));
		templates.add(new Template( "false", "false"));
		templates.add(new Template( "$fa", "minimum angle", ""));
		templates.add(new Template( "$fs", "minimum size", ""));
		templates.add(new Template( "$t", "animation step", ""));
		templates.add(new Template( "$fn", "number of fragments", ""));
		//templates.add(new Template( "$fn", "number of fragments", ""));
		templates.add(new Template( "$vpr", "viewport rotation angles in degrees", ""));


		templates.add(new Template( "$vpt", "viewport translation ", ""));
		templates.add(new Template( "$vpd", "viewport camera distance ", ""));
		templates.add(new Template( "$children", "number of module children ", ""));


		for(String val : ("abs," + 
				"sign," + 
				"sin," + 
				"cos," + 
				"tan," + 
				"acos," + 
				"asin," + 
				"atan," + 
				"atan2," + 
				"floor," + 
				"round," + 
				"ceil," + 
				"ln," + 
				"len," + 
				"let," + 
				"log," + 
				"pow," + 
				"sqrt," + 
				"exp," + 
				"rands," + 
				"min," + 
				"max").split("[,]")){
			templates.add(new Template(val,val+"-Math",val+"(${value} )"));
		}

		for(String val : ( 
				"concat," + 
				"lookup," + 
				"str," + 
				"chr," + 
				"search," + 
				"version," + 
				"version_num," + 
				"norm," + 
				"cross").split("[,]")) {
					templates.add(new Template(val,val+"-function",val+"(${value} )"));
				}


	}
	
	public static Configuration createDefault() {
		Configuration ret = new Configuration();
		ret.backupFolder = "backup";
		ret.maxBackup=6;
		ret.maxRecent = 10;
		ret.minVariableNameLength = 2;
		ret.templates = new ArrayList<>();
		populateDefault(ret.templates);

		return ret;
	}


	public static void main(String[] args) throws IOException {
		boolean create = true;
		if( create ) {
			Configuration config = createDefault();
			config.save();
		} else {
			Configuration config = getInstance();
			System.out.println("Number or templates="+config.templates.size());
			System.out.println("maxBackup="+config.maxBackup);
			System.out.println("maxRecent="+config.maxRecent);
			System.out.println("minVariableNameLength="+config.minVariableNameLength);
			System.out.println("backupFolder="+config.backupFolder);
			System.out.println("execPath="+config.execPath);
			List<Template> temp = config.getTemplates();
			List<Template> temp2 = config.getTemplates();
			System.out.println("eq="+temp.equals(temp2));
		}
	}

}
