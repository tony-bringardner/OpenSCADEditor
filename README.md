# OpenSCADEditor
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
</head>
<body>
<h1>Java based OpenSCAD editor</h1>

<h1>
OpenSCADEditor is a java based editor designed to improve productivity for users of OpenSCAD.<BR> <a href="http://www.openscad.org/">OpenSCAD</a> is software for creating solid 3D CAD objects. 

</h1>
<h2>Features</h2>
<ul>
	<li>Syntax highlighting for the OpenSCAD language.
	<li>Code folding
	<li>Code completion using Templates maintained by the user. 
	<li>Spell checker (checks spelling in comments using an English dictionary)
	<li>Search and Replace
	<li>Undo and Redo
	<li>Links with OpenSCAD for easy preview and exporting 3d models.
	<li>Support for <a href="http://velocity.apache.org/engine/1.7/user-guide.html">Apache Velocity </a>
</ul>

Much of the functionality provided here is due to the <em>AWESOME</em> <a href="https://github.com/bobbylight/RSyntaxTextArea">RSyntaxTextArea</a> library.

<h3>Keyboard Shortcuts (Hot Keys)</h3>
<table border="1" cellpadding="3" cellspacing="0" style="border: 1px solid gray; border-collapse: collapse;">
<tbody><tr style="background: #ececec; border: 1px solid gray">
<th>Function
</th>
<th>Shortcut Key
</th></tr>

<tr>
	<td> Auto-complete  </td>
	<td> Ctrl + Space </td>
</tr>

<tr>
	<td> Expand the current fold  </td>
	<td> Ctrl + ADD (+ on key pad) </td>
</tr>
<tr>
	<td> Expand all folds  </td>
	<td> Ctrl + Shift + ADD (+ on key pad) </td>
</tr>
<tr>
	<td> Collapse the current fold  </td>
	<td> Ctrl + MINUS (- on key pad) </td>
</tr>

<tr>
	<td> Collapse all folds  </td>
	<td> Ctrl + Shift + MINUS (- on key pad) </td>
</tr>

<tr>
<td> Undo last action </td>
<td> Ctrl + Z
</td></tr>


<tr>
<td> Redo last action </td>
<td> Ctrl + Y
</td></tr>


<tr>
<td> Cut selected text 	</td>
<td> Ctrl + X
</td></tr>


<tr>
<td> Copy selected text </td>
<td> Ctrl + C
</td>
</tr>


<tr>
<td> Paste text from clipboard </td>
<td> Ctrl + V
</td></tr>


<tr>
<td> Select all text </td>
<td> Ctrl + A
</td></tr>


<tr>
<td> Preview in OpenSCAD</td>
<td> F5 or Ctrl + B
</td></tr>

<tr>
	<td>Find / Replace </td>
	<td> Ctrl + F</td>
</tr>

<tr>
	<td>Format selected text </td>
	<td>Ctrl + I</td>
</tr>

<tr>
	<td>Open a file </td>
	<td>Ctrl + O</td>
</tr>

<tr>
	<td>Save the current file </td>
	<td>Ctrl + S</td>
</tr>

<tr>
	<td>Find matching brace ({ or }) </td>
	<td>Ctrl + P</td>
</tr>


<tr>
	<td>Jump to declaration of selected text </td>
	<td> F3</td>
</tr>

<tr>
	<td>Toggle comment (current line or selection) </td>
	<td> Ctrl + C</td>
</tr>

<tr>
	<td>Close all comments </td>
	<td> Ctrl + Shift + C</td>
</tr>

<tr>
	<td>Go to line number </td>
	<td> Ctrl + G</td>
</tr>

</tbody></table>
<br>
<h2>Using Velocoty</h2>
Velocity is a template engine. It is a simple yet powerful template language that let's you create reusable code snippets.
The velocity code is evaluated and expanded before sending to OpenSCAD for preview or exporting.
OpenscadEditorV1.1.jar was created with velocity support.

</h1>



<br></body>
</html>
