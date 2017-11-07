var PDF_DIFF = PDF_DIFF || {};

PDF_DIFF.diff_report_view = function(report_data) {
	var dpi = 96;
	
	var file_format = report_data.FileFormat;
	var afp_tle_compare_result = report_data.afp_tle_compare_result;
	var afp_tle_compare_data = report_data.afp_tle_compare_data;
	var afp_nop_compare_result = report_data.afp_nop_compare_result;
	var afp_nop_compare_data = report_data.afp_nop_compare_data;
	
	var ShowDifferentPagesOnly = report_data.ShowDifferentPagesOnly;
	var Rendering_Resolution = report_data.Rendering_Resolution;
	var Base_Stroke_Color = report_data.Base_Stroke_Color;
	var Test_Stroke_Color = report_data.Test_Stroke_Color;
	var Test_Fill_Color = report_data.Test_Fill_Color;
	var Base_Fill_Color = report_data.Base_Fill_Color;
	var xor_images = report_data.page_xor_images;

	var diff_page_count = report_data.diff_page_count;
	var diff_page_nums = report_data.diff_page_nums;
	var diff_accepted_page_nums = report_data.diff_accepted_page_nums;
	
	var base_pdf_json_obj = report_data.base_pdf_json_obj ;
	var test_pdf_json_obj = report_data.test_pdf_json_obj;
	var diff_content_json_obj = report_data.diff_content_json_obj;

	var tree =
	[
	{
		text: "Page",
		color: "#000000",
		backColor: "#FFFFFF",
		selectable: true,
		state: {
			checked: true,
			disabled: false,
			expanded: true,
			selected: false
		},
		tags: ['available'],

		nodes: [
		{
			text: "Text",
			nodes: [],
			tags: ['0'],
		},
		{
			text: "Image",
			nodes: [],
			tags: ['0'],
		},
		{
			text: "Graphics",
			nodes: [],
			tags: ['0'],
		},
		{
			text: "Annot",
			nodes: [],
			tags: ['0'],
		},
		{
			text: "Accept Difference",
			nodes: [],
			tags: ['0'],
		},
		]
	}
	];
	
	var page_view_paras = {
		PageNo : 0,
		BasePageWidth : 0,
		BasePageHeight : 0,
		TestPageHeight : 0,
		TestPageWidth : 0,
		BaseIsBlank : false,
		TestIsBlank : false,
		DiffContent : null,

		NodeText : "",
		Text : "",
			
		fitToPage : true,
		rectangleSelection : false,
		magnifier : true,
		mousePressed : false,
		rectStartPoint : null,
		rectEndPoint : null,
		rectSelectoin : false,
		
		pageScale : "FitToPage",
		baseCanvasScale : 1,
		testCanvasScale : 1,
		
		pxToPt : function(x, y, isControl) {
			var scale = isControl ? this.baseCanvasScale : this.testCanvasScale;
			return {
				x: Math.round(toPoint(x / scale)),
				y: Math.round(toPoint(y / scale)),
			};
		}
	};

	this.onload = function() {
		initDocInfoDialog();
		
		var base_pdf_span = document.getElementById("base_pdf_name");
		base_pdf_span.textContent = base_pdf_json_obj.fileName;
		$( "#base_pdf_name" ).click(function() {
			if ($( "#doc_info_dialog" ).dialog("isOpen")) {
				return;
			}
			
			$( "#doc_info_dialog" ).dialog("open");
		});

		var test_pdf_span = document.getElementById("test_pdf_name");
		test_pdf_span.textContent = test_pdf_json_obj.fileName;
		$( "#test_pdf_name" ).click(function() {
			if ($( "#doc_info_dialog" ).dialog("isOpen")) {
				return;
			}
			
			$( "#doc_info_dialog" ).dialog("open");
		});

		var max_page_count = base_pdf_json_obj.pageCount > test_pdf_json_obj.pageCount ? 
			base_pdf_json_obj.pageCount : test_pdf_json_obj.pageCount;
		var page_count_span = document.getElementById("page_count");
		page_count_span.textContent = max_page_count;

		var sum = "";
		if (diff_page_count == 0) {
			sum = "These two " + file_format.toUpperCase() + " files are the same!";
		} else if (diff_page_count == 1) {
			sum = "Found <span style=\"color:red; font-size: 1.5em\">" + diff_page_count + "</span> different page!";
		} else {
			sum = "Found <span style=\"color:red; font-size: 1.5em\">" + diff_page_count + "</span> different pages!";
		}
		
		if (file_format == "afp") {
			if (afp_tle_compare_result) {
				$("#afp_tle_summay").text("All TLEs are the same!");
				$("#afp_tle_summay").css( "color", "white" );
			} else {
				$("#afp_tle_summay").text("Found difference TLE!");
				$("#afp_tle_summay").css( "color", "red" );
			}
			
			$("#afp_tle_summay").show();
			initTLEDialog();
			$( "#afp_tle_summay" ).click(function() {
				if ($( "#tle_dialog" ).dialog("isOpen")) {
					return;
				}
				
				$( "#tle_dialog" ).dialog("open");
			});
			
			if (afp_nop_compare_result) {
				$("#afp_nop_summay").text("All comments are the same!");
				$("#afp_nop_summay").css( "color", "white" );
			} else {
				$("#afp_nop_summay").text("Found difference comments!");
				$("#afp_nop_summay").css( "color", "red" );
			}
			
			$("#afp_nop_summay").show();
			initNopDialog();
			$( "#afp_nop_summay" ).click(function() {
				if ($( "#nop_dialog" ).dialog("isOpen")) {
					return;
				}
				
				$( "#nop_dialog" ).dialog("open");
			});
		}
		
		var diff_summary_span = document.getElementById("diff_summary");
		diff_summary_span.innerHTML = sum;

		var tableBody = document.getElementById("page_list_table").getElementsByTagName("tbody")[0];
		var diffColor = "#FF0000", sameColor = "rgb(111, 111, 111)";
		
		var invisibleRow = 0;
		for (var i = 0; i < max_page_count; i++) {
			
			if  (ShowDifferentPagesOnly && diff_page_nums.indexOf(i) < 0 && !xor_images.hasOwnProperty(i + "")) {
				invisibleRow++;
				continue;
			}
			
			var pageRow = tableBody.insertRow(tableBody.rows.length);
			var cell = pageRow.insertCell(0);
			if (i >= base_pdf_json_obj.pageCount) {
				var text = document.createTextNode("NA");
			} else {
				var text = document.createTextNode("Page " + (i + 1));	
			}
			
			cell.appendChild(text);

			if (diff_page_nums.indexOf(i) >= 0) {
				cell.style.color = diffColor;
			} else {
				cell.style.color = sameColor;
			}

			var cell = pageRow.insertCell(1);
			if (i >= test_pdf_json_obj.pageCount) {
				var text = document.createTextNode("NA");
			} else {
				var text = document.createTextNode("Page " + (i + 1));	
			}
			cell.appendChild(text);

			if (diff_page_nums.indexOf(i) >= 0) {
				cell.style.color = diffColor;
			} else {
				cell.style.color = sameColor;
			}

			// show xor image
			var cell = pageRow.insertCell(2);
			var key = i + "";
			if (xor_images.hasOwnProperty(key)) {
				var href = "images/" + xor_images[key];
				var x = document.createElement("A");
				if (diff_accepted_page_nums.indexOf(i) >= 0) {
					x.style.color = "green";
				} else {
					x.style.color = "red";
				}
				
				var t = document.createTextNode("Show");
				x.setAttribute("href", href);
				x.setAttribute("target", "_blank");
				x.appendChild(t);
			} else {
				var x = document.createTextNode("Same");
			}
			cell.appendChild(x);
			cell.style.textAlign = "center";
			pageRow.onclick = pageSelectHandler(i - invisibleRow);
		}

		$(function() {
			var options = {
				bootstrap2: false, 
				showTags: true,
				levels: 5,
				data: tree};
				$('#treeview').treeview(options);

				$('#treeview').on('nodeSelected', function(e, node) {
				});
			}
		);
		
		// build context menu on Canvas
		buildContextMenu('.base_page_canvas');
		buildContextMenu('.test_page_canvas');
		
		$( "#rect_dialog" ).dialog({ autoOpen: false , width: 600});
		$( "#tle_dialog" ).dialog({ autoOpen: false , width: 800});
		$( "#nop_dialog" ).dialog({ autoOpen: false , width: 800});
		$( "#doc_info_dialog" ).dialog({ autoOpen: false , width: 800});
	};
	
	var initDocInfoDialog = function() {
		var tableBody = document.getElementById("doc_info_table").getElementsByTagName("tbody")[0];

		var props = ["title", "author", "subject", "keywords", "created", "modified", "producer", "version", "location", "fileSize", "pageCount"];
		
		for (var i = 0; i < props.length; i++) {
			var pageRow = tableBody.insertRow(tableBody.rows.length);
			
			var val_0, val_1, val_2;
			var prop = props[i];
			
			if (prop === "fileSize") {
				val_0 = "File Size";
			} else if (prop === "pageCount") {
				val_0 = "Number of Pages";
			} else {
				val_0 = prop.charAt(0).toUpperCase() + prop.slice(1);
			}
			
			val_1 = base_pdf_json_obj[prop];
			val_2 = test_pdf_json_obj[prop];
			
			var cell = pageRow.insertCell(0);
			cell.style.color = "white";
			cell.style.fontWeight  = "Bold";
			cell.style.wordWrap = "break-word";
			cell.style.padding = "5px 5px 5px 5px";
			var text = document.createTextNode(val_0);
			cell.appendChild(text);
			
			cell = pageRow.insertCell(1);
			cell.style.color = "white";
			cell.style.wordWrap = "break-word";
			cell.style.padding = "5px 5px 5px 5px";
			var text = document.createTextNode(val_1);
			cell.appendChild(text);
			
			cell = pageRow.insertCell(2);
			cell.style.color = "white";
			cell.style.wordWrap = "break-word";
			cell.style.padding = "5px 5px 5px 5px";
			var text = document.createTextNode(val_2);
			cell.appendChild(text);
			
		}
	};
	
	var initNopDialog = function() {
		var max_nop_count = afp_nop_compare_data.length;
		if (max_nop_count == 0) {
			$( "#nop_info" ).text("Not found comments in the AFP files!");
			return;
		} else {
			$( "#nop_info" ).html("Found <span class='highlight'>" + max_nop_count + "</span> comments in the AFP files!");
		}
		
		var tableBody = document.getElementById("nop_table").getElementsByTagName("tbody")[0];
		var diffColor = "red", sameColor = "white";
		for (var i = 0; i < max_nop_count; i++) {
			var pageRow = tableBody.insertRow(tableBody.rows.length);
			
			var cell = pageRow.insertCell(0);
			var equals = afp_nop_compare_data[i][0];
			if (equals) {
				cell.style.color = sameColor;
			} else {
				cell.style.color = diffColor;
			}

			var text = document.createTextNode(i + 1 + "");
			cell.appendChild(text);
			
			cell = pageRow.insertCell(1);
			buildNopNode(cell, afp_nop_compare_data[i][1], equals, diffColor, sameColor);
			
			cell = pageRow.insertCell(2);
			buildNopNode(cell, afp_nop_compare_data[i][2], equals, diffColor, sameColor);
		}
	};
	
	var buildNopNode = function(cell, str, equals, diffColor, sameColor) {
		if (equals) {
			cell.style.color = sameColor;
		} else {
			cell.style.color = diffColor;
		}
		cell.style.wordWrap = "break-word";
		cell.style.padding = "5px 5px 5px 5px";
		
		var text = document.createTextNode(str);
		cell.appendChild(text);
	};
	
	var initTLEDialog = function() {
		var max_tle_count = afp_tle_compare_data.length;
		if (max_tle_count == 0) {
			$( "#TLE_info" ).text("Not found TLE items in the AFP files!");
			return;
		} else {
			$( "#TLE_info" ).html("Found <span class='highlight'>" + max_tle_count + "</span> TLE items in the AFP files!");
		}
		
		var tableBody = document.getElementById("TLE_table").getElementsByTagName("tbody")[0];
		var diffColor = "red", sameColor = "white";
		for (var i = 0; i < max_tle_count; i++) {
			var pageRow = tableBody.insertRow(tableBody.rows.length);
			
			var cell = pageRow.insertCell(0);
			var equals = afp_tle_compare_data[i][0];
			if (equals) {
				cell.style.color = sameColor;
			} else {
				cell.style.color = diffColor;
			}

			var text = document.createTextNode(i + 1 + "");
			cell.appendChild(text);
			
			cell = pageRow.insertCell(1);
			buildTLENode(cell, afp_tle_compare_data[i][1], equals, diffColor, sameColor);
			
			cell = pageRow.insertCell(2);
			buildTLENode(cell, afp_tle_compare_data[i][2], equals, diffColor, sameColor);
		}
	};
	
	var buildTLENode = function(cell, arr, equals, diffColor, sameColor) {
		if (equals) {
			cell.style.color = sameColor;
		} else {
			cell.style.color = diffColor;
		}
		cell.style.wordWrap = "break-word";
		cell.style.padding = "5px 5px 5px 5px";
		var textColor = "rgb(111, 111, 111)";
		
		var text = document.createTextNode("Name = ");
		var span = document.createElement('span');
		span.appendChild(text);
		span.style.color = textColor;
		cell.appendChild(span);
		
		var text = document.createTextNode(arr[0]);
		cell.appendChild(text);
		
		cell.appendChild(document.createElement("br"));

		text = document.createTextNode("Value = ");
		span = document.createElement('span');
		span.appendChild(text);
		span.style.color = textColor;
		cell.appendChild(span);
		
		var text = document.createTextNode(arr[1]);
		cell.appendChild(text);
	};
	
	var buildContextMenu = function(className) {
		$(function() {
			$.contextMenu({
				selector: className, 
				callback: function(key, options) {
					if (key === "rect") {
						page_view_paras.magnifier = false;
						page_view_paras.rectangleSelection = true;
					} else {
						page_view_paras.magnifier = true;
						page_view_paras.rectangleSelection = false;
					}
				},
				items: {
					fitPage: {name: "Fit Page", icon: ""},
					"sep1": "---------",
					magnifier : {name: "Magnifier", icon: ""},
		            rect: {name: "Rectangle Selection", icon: ""},
				}
			});

		});
	};

	var updatePageSelection = function(pageNo) {
		var tableBody = document.getElementById("page_list_table").getElementsByTagName("tbody")[0];

		for (var i = 0; i < tableBody.rows.length; i++) {
			var td = tableBody.rows[i].cells[0];
			var td_1 = tableBody.rows[i].cells[1];
			if (i == pageNo) {
				updateCellColor(td, true);
				updateCellColor(td_1, true);
			} else {
				updateCellColor(td, false);
				updateCellColor(td_1, false);
			}
		}
	}

	var updateCellColor = function(td, selected) {
		if (selected) {
			td.style.backgroundColor  = "lightgray";
			td.style.fontWeight  = "Bold";
			td.className += " selected";
		} else {
			td.style.backgroundColor = "rgb(238, 238, 238)";
			td.style.fontWeight  = "normal";
			td.classList.remove('selected');
		}
	}
	
	var pageSelectHandler = function(pageNo) {
		return function() {
			updatePageSelection(pageNo);
			
			page_view_paras["PageNo"] = pageNo;
			page_view_paras["DiffContent"] = null;

			if (pageNo < base_pdf_json_obj.pageCount) {
				page_view_paras["BasePageWidth"] = base_pdf_json_obj.pages[pageNo].width;
				page_view_paras["BasePageHeight"] = base_pdf_json_obj.pages[pageNo].height;
				page_view_paras["BaseIsBlank"] = false;
			} else {
				page_view_paras["BasePageWidth"] = 0;
				page_view_paras["BasePageHeight"] = 0;
				page_view_paras["BaseIsBlank"] = true;
			}

			if (pageNo < test_pdf_json_obj.pageCount) {
				page_view_paras["TestPageWidth"] = test_pdf_json_obj.pages[pageNo].width;
				page_view_paras["TestPageHeight"] = test_pdf_json_obj.pages[pageNo].height;
				page_view_paras["TestIsBlank"] = false;
			} else {
				page_view_paras["TestPageWidth"] = 0;
				page_view_paras["TestPageHeight"] = 0;
				page_view_paras["TestIsBlank"] = true;
			}

			drawTree(pageNo);
			updatePageView();
		};
	};
	
	var buildBaseCanvas = function(paras, cellW) {
		var pageNo = page_view_paras["PageNo"];
		
		var w = 0, h = 0;
		var baseCanvas = document.getElementById("base_page_canvas");
		var cell = document.getElementById("base_page_td");
			
		if (paras["BaseIsBlank"]) {
			w = toPixel(paras["TestPageWidth"]);
			h = toPixel(paras["TestPageHeight"]);
		} else {
			w = toPixel(paras["BasePageWidth"]);
			h = toPixel(paras["BasePageHeight"]);
		}

		if (paras.pageScale == "FitToPage") {
			paras.baseCanvasScale = cellW / w;
		}
		
		baseCanvas.width = w * paras.baseCanvasScale;
		baseCanvas.height = h * paras.baseCanvasScale;
			
		if (paras["BaseIsBlank"]) {
			drawBlankPage(baseCanvas);
		} else {
			var imageTag = base_pdf_json_obj.pages[pageNo].imageTag;
			drawPage(cell, imageTag, baseCanvas, paras.baseCanvasScale);
			
			// addCanvasMouseListener(baseCanvas);
		}
	};

	var buildTestCanvas = function(paras, cellW) {
		var pageNo = page_view_paras["PageNo"];
		
		var w = 0, h = 0;
		var testCanvas = document.getElementById("test_page_canvas");
		var cell = document.getElementById("test_page_td");
		
		if (paras["TestIsBlank"]) {
			w = toPixel(paras["BasePageWidth"]);
			h = toPixel(paras["BasePageHeight"]);
		} else {
			w = toPixel(paras["TestPageWidth"]);
			h = toPixel(paras["TestPageHeight"]);
		}
		if (paras.pageScale == "FitToPage") {
//			testCanvasScale = cellW / w;
			paras.testCanvasScale = paras.baseCanvasScale;
		}
		
		testCanvas.width = w * paras.testCanvasScale;
		testCanvas.height = h * paras.testCanvasScale;
		
		if (paras["TestIsBlank"]) {
			drawBlankPage(testCanvas);
		} else {
			var imageTag = test_pdf_json_obj.pages[pageNo].imageTag;
			drawPage(cell, imageTag, testCanvas, paras.testCanvasScale);

			// addCanvasMouseListener(testCanvas);
		}
	}
	
	var updatePageView = function() {
		var t = document.getElementById("canvas_table");
		var tw = t.clientWidth;
		
		var baseW;
		if (page_view_paras["BaseIsBlank"]) {
			baseW = toPixel(page_view_paras["TestPageWidth"]);
		} else {
			baseW = toPixel(page_view_paras["BasePageWidth"]);
		}
		var testW;
		if (page_view_paras["TestIsBlank"]) {
			testW = toPixel(page_view_paras["BasePageWidth"]);
		} else {
			testW = toPixel(page_view_paras["TestPageWidth"]);
		}
		var scaleBase = baseW / (baseW + testW);
		var canvasW = parseInt(tw * scaleBase);

		buildBaseCanvas(page_view_paras, canvasW);
		buildTestCanvas(page_view_paras, tw - canvasW);

		var item = page_view_paras["DiffContent"];
		$("#attribute_table tbody tr").remove();
		if ((typeof(item) !== 'undefined') && (item !== null)) {
			updateAttributeTable(item);
		}
	};

	var drawTree = function(pageNo) {
		page_view_paras["PageNo"] = pageNo;
		tree[0].tags = ["Page " + (pageNo + 1)];
		initTreeData(pageNo);
		$(function() {
			var options = {
				bootstrap2: false, 
				showTags: true,
				levels: 5,
				data: tree};
				$('#treeview').treeview(options);

				$('#treeview').on('nodeSelected', function(e, node) {
					var parent = $('#treeview').treeview('getParent', node);
					if (parent !== undefined) {
						page_view_paras["Text"] = parent['text'];
					}

					var item = node['item'];

					page_view_paras["DiffContent"] = item;
					page_view_paras["NodeText"] = node['text'];
					updatePageView();
			});
		});
	};

	var initTreeData = function(pageNo) {
		tree[0].nodes[0].nodes.length = 0;
		tree[0].nodes[0].tags = [0]; // Text
		tree[0].nodes[1].nodes.length = 0;
		tree[0].nodes[1].tags = [0]; // Image
		tree[0].nodes[2].nodes.length = 0;
		tree[0].nodes[2].tags = [0]; // Graphics
		tree[0].nodes[3].nodes.length = 0;
		tree[0].nodes[3].tags = [0]; // Annot
		tree[0].nodes[4].nodes.length = 0;
		tree[0].nodes[4].tags = [0]; // Accept Difference
		
		for (var i = 0; i < diff_content_json_obj.length; i++) {
			var num = diff_content_json_obj[i].PageNo;
			if (pageNo == num) {
				var result = diff_content_json_obj[i].Result;
				
				// update Page node
				var item = result.Page[0];
				tree[0].item = item;
				
				// update Text node
				tree[0].nodes[0].tags = [result.Text.length];
				for (var j = 0; j < result.Text.length; j++) {
					var item = result.Text[j];
					var text = findShowText(item.Attributes);
					
					var treeNodes = tree[0].nodes[0].nodes;
					var newItem = {"text" : text, "item" : item};
					treeNodes.push(newItem);
				}
				
				// update Image node
				tree[0].nodes[1].tags = [result.Image.length];
				for (var j = 0; j < result.Image.length; j++) {
					var item = result.Image[j];
					var text = "image-" + j;
					
					var imageNodes = tree[0].nodes[1].nodes;
					var newItem = {"text" : text, "item" : item};
					imageNodes.push(newItem);
				}
				
				// update Graphics node
				tree[0].nodes[2].tags = [result.Path.length];
				for (var j = 0; j < result.Path.length; j++) {
					var item = result.Path[j];
					var text = "Graphics-" + j;
					
					var pathNodes = tree[0].nodes[2].nodes;
					var newItem = {"text" : text, "item" : item};
					pathNodes.push(newItem);
				}
				
				// update Annot node
				tree[0].nodes[3].tags = [result.Annot.length];
				for (var j = 0; j < result.Annot.length; j++) {
					var item = result.Annot[j];
					var text = "Annot-" + j;
					
					var annotNodes = tree[0].nodes[3].nodes;
					var newItem = {"text" : text, "item" : item};
					annotNodes.push(newItem);
				}
				
				// update Accept Difference node
				tree[0].nodes[4].tags = [result.Accept_Difference.length];
				for (var j = 0; j < result.Accept_Difference.length; j++) {
					var item = result.Accept_Difference[j];
					var text = "Accept Difference-" + j;
					
					var acceptDiffNodes = tree[0].nodes[4].nodes;
					var newItem = {"text" : text, "item" : item};
					acceptDiffNodes.push(newItem);
				}
			}
		}
	};

	var drawBlankPage = function(canvas) {
		var ctx = canvas.getContext("2d");
		ctx.clearRect(0, 0, canvas.width, canvas.height);
		ctx.save();
		ctx.fillStyle = "gray";
		ctx.fillRect(1, 1, canvas.width - 1, canvas.height - 1);
		ctx.rect(0, 0, canvas.width, canvas.height);
		ctx.strokeStyle = 'red';
		ctx.stroke();

		ctx.translate(72, canvas.height / 2);
		ctx.scale(3, 3);
		ctx.font = "16pt Calibri";
		ctx.fillStyle = 'red';
		ctx.fillText("NOT FOUND", 0, 0);
		
		ctx.restore();
	};

	var getMousePos = function(canvas, evt) {
		var rect = canvas.getBoundingClientRect();
		return {
			x: roundMill(evt.clientX - rect.left),
			y: roundMill(evt.clientY - rect.top)
		};
	};

	var updateAttributeTable = function(item) {
		$("#attribute_table tbody tr").remove();
		var tableBody = document.getElementById("attribute_table").getElementsByTagName("tbody")[0];
		for (var i = 0; i < item.Attributes.length; i++) {
			var attr = item.Attributes[i];
			
			var color = "black";
			if (!attr.Equals) {
				color = "red";
			} else if (attr.Equals && !(attr.Value[0] == attr.Value[1])) {
				color = "green";
			}
			
			var attrRow = tableBody.insertRow(tableBody.rows.length);
			
			var cell = attrRow.insertCell(0);
			var text = document.createTextNode(attr.Key);
			cell.appendChild(text);
			cell.style.textAlign = "left";
			cell.style.fontWeight = "bold";
			cell.style.color = color;
			
			var cell = attrRow.insertCell(1);
			var text = document.createTextNode(attr.Value[0]);
			cell.appendChild(text);
			cell.style.textAlign = "center";
			cell.style.color = color;
			
			var cell = attrRow.insertCell(2);
			var text = document.createTextNode(attr.Value[1]);
			cell.appendChild(text);
			cell.style.textAlign = "center";
			cell.style.color = color;
		}
	};

	var findShowText = function(attributes) {
		var text;
		for (var i = 0; i < attributes.length; i++) {
			if (attributes[i].Key == "Text") {
				text = attributes[i].Value[0];
				if (attributes[i].Value[0] == "") {
					text = attributes[i].Value[1];
				}
			}
		}
		return text;
	};


	var drawPage = function(cell, imageTag, canvas, scale) {
		ctx = canvas.getContext("2d");
		drawPageImage(cell, imageTag, ctx, canvas, scale);
	};


	var drawPageImage = function(cell, imageTag, ctx, canvas, scale) {
		var img = new Image();
		img.src = "images/" + imageTag;

		img.onload = function() {
			var w = canvas.width;
			var h = canvas.height;
			var s = w / img.width;

			cell.style.backgroundImage = "url(" + "images/" + imageTag + ")";
			cell.style.backgroundSize = w + "px " + h + "px";
			cell.style.backgroundRepeat = "no-repeat";

			addCanvasMouseListener(canvas, img, scale);

			repaintCanvas(canvas);
		}
	};
	
	var repaint = function() {
		repaintCanvas(base_page_canvas);
		repaintCanvas(test_page_canvas);
	};
	
	var repaintCanvas = function(canvas) {
		var ctx = canvas.getContext("2d");
		var w = canvas.width;
		var h = canvas.height;
		
		ctx.clearRect(0, 0, w, h);
		
		ctx.save();
		ctx.lineWidth="1";
		ctx.strokeStyle="red";
		ctx.strokeRect(1, 1, w - 1, h - 1);
		
		ctx.fillStyle = "red";
		ctx.font = "16pt Calibri";
		var tag = canvas.id === "base_page_canvas" ? "Control" : "Test";
		ctx.fillText(tag, 2, 16);
		ctx.restore();
	
		var item = page_view_paras["DiffContent"];
		var category = page_view_paras["Text"];
		if ((typeof(item) !== 'undefined') && (item !== null)) {
			drawDiffContentOutline(category, item);
		}
	};

	var addCanvasMouseListener = function(canvas, img, scale) {
		canvas.style.cursor = 'crosshair';
		
		canvas.addEventListener('mousemove', function(evt) {
			if (page_view_paras.magnifier) { // show magnifier
				var mousePos = getMousePos(canvas, evt);
				var zoomCtx = zoom_canvas.getContext("2d");
				zoomCtx.fillStyle = "white";
				zoomCtx.fillRect(0, 0, zoom_canvas.width, zoom_canvas.height);

				var s = scale / (Rendering_Resolution / dpi);
				var x = mousePos.x / s;
				var y = mousePos.y / s;
				var w =  zoom_canvas.width;
				var h = zoom_canvas.height;

				zoomCtx.drawImage(img, x, y, w, h, 0, 0, w, h);
				zoomCtx.drawImage(canvas, mousePos.x, mousePos.y, w, h, 0, 0, w / s, h / s);
				zoom_canvas.style.top = evt.pageY + 2 + "px";
				zoom_canvas.style.left = evt.pageX + 2 + "px";
				zoom_canvas.style.display = "block";
			} else if (page_view_paras.rectangleSelection) { // show current coordination
				if ($( "#rect_dialog" ).dialog("isOpen")) {
					return;
				}
				repaint();
				if (page_view_paras.mousePressed) {
					var mousePos = getMousePos(canvas, evt);
					var begin = page_view_paras.rectStartPoint;
					var end = mousePos;
					
					var ctx = canvas.getContext("2d");
					drawRect(ctx, begin, end);
				} else {
					page_view_paras.rectSelectoin = false;
					var mousePos = getMousePos(canvas, evt);
					var ctx = canvas.getContext("2d");
					ctx.save();
					ctx.fillStyle = "red";
					ctx.font = "16pt Calibri";
					
					var isControl = canvas.id === "base_page_canvas";
					var pos = page_view_paras.pxToPt(mousePos.x, mousePos.y, isControl);
					
					ctx.fillText(pos.x + ", " + pos.y, mousePos.x + 2, mousePos.y - 2);
					ctx.restore();
				}
			} else {
				return;
			}
		}, false);

		canvas.addEventListener("mousedown", function(evt) {
			if (!page_view_paras.rectangleSelection) {
				return;
			}
			var mousePos = getMousePos(canvas, evt);
			page_view_paras.mousePressed = true;
			page_view_paras.rectStartPoint = mousePos;
		}, false);
		
		canvas.addEventListener("mouseup", function(evt) {
			if (page_view_paras.rectangleSelection) {
				var begin = page_view_paras.rectStartPoint;
				var end = getMousePos(canvas, evt);
				
				page_view_paras.mousePressed = false;
				page_view_paras.rectEndPoint = end;
				
				var ctx = canvas.getContext("2d");
				if (drawRect(ctx, begin, end)) {
					page_view_paras.rectSelectoin = true;
					
					var ph = canvas.id === "base_page_canvas" ? page_view_paras.BasePageHeight : page_view_paras.TestPageHeight;
					buildRectSelectionMessage(begin, end, ph, canvas.id === "base_page_canvas");
					$( "#rect_dialog" ).dialog("open");
				}
			}
		}, false);
		
		canvas.addEventListener("mouseout", function() {
			if (page_view_paras.magnifier) {
				zoom_canvas.style.display = "none";
			} else if (page_view_paras.rectangleSelection) {
				if (!page_view_paras.rectSelectoin) {
					repaint();
				}
			}
		});
	};
	
	var buildRectSelectionMessage = function(begin, end, pageHeight, isControl) {
		var beginPos = page_view_paras.pxToPt(begin.x, begin.y, isControl);
		var endPos = page_view_paras.pxToPt(end.x, end.y, isControl);
		
		var x0 = beginPos.x;
		var y0 = beginPos.y;
		var x1 = endPos.x;
		var y1 = endPos.y;
		var w = Math.round(Math.abs(x1 - x0));
		var h = Math.round(Math.abs(y1 - y0));
		
		var text = "{" + page_view_paras.PageNo + " " + (isControl ? "control" : "test") + ", ";
		var rect = Math.round(x0) + " " + Math.round(pageHeight - y0 - h) + " " + w + " " + h;
		text += "[" + rect + "]";
		text += "}";
		text = "<span style=\"font-size: 2em; color: yellow\">" + text + "</span>";
		$( "#rect_dialog" ).html("Copy following yellow line of text into config.properties " +
				"after configuration acceptanceDifferenceArea. Then re-compare files."  
				+ "<br>" + text);
	};
		
	var drawRect = function(ctx, begin, end) {
		if (end.x < begin.x || end.y < begin.y) {
			return false;
		}
		
		var w = Math.abs(end.x - begin.x);
		var h = Math.abs(end.y - begin.y);

		drawAcceptDiffRegion(ctx, begin.x, begin.y, w, h);
		
		if (w < 5 || h < 5) {
			return false;
		}
		
		return true;
	};
	
	var drawAcceptDiffRegion = function(ctx, x, y, w, h) {
		ctx.save();
		ctx.lineWidth = "1";
		ctx.strokeStyle = "green";
		ctx.fillStyle = "rgba(255, 255, 0, 0.2)";
		
		ctx.strokeRect(x, y, w, h);
		ctx.fillRect(x, y, w, h);
		ctx.restore();
	};

	var drawDiffContentOutline = function(category, item) { // arr[base, test]
		var outlineArr = item.Outline;
		var subOutlineArr = item.SubOutline;
		
		var basePaintOperator, testPaintOperator;
		var baseLineWidth, testLineWidth;
		if (category === "Graphics") {
			for (var i = 0; i < item.Attributes.length; i++) {
				var attr = item.Attributes[i];
				if ("Line Width" === attr.Key) {
					baseLineWidth = attr.Value[0];
					testLineWidth = attr.Value[1];					
				} else if ("Paint Operator" === attr.Key) {
					basePaintOperator = attr.Value;
					testPaintOperator = attr.Value;
				}
			}
		}
		
		var baseRect = outlineArr[0];
		var testRect = outlineArr[1];
		
		var baseSubRectArr = subOutlineArr[0];
		var testSubRectArr = subOutlineArr[1];
		
		var baseAcceptRegion = item.AcceptDiffRegion[0];
		var testAcceptRegion = item.AcceptDiffRegion[1];
		
		var baseCanvas = document.getElementById("base_page_canvas");
		var baseCtx = baseCanvas.getContext("2d");
		
		var testCanvas = document.getElementById("test_page_canvas");
		var testCtx = testCanvas.getContext("2d");
		
		var notFoundColor = "blue";
		
		if (baseRect.length > 0) {
			baseCtx.save();
			baseCtx.scale(page_view_paras.baseCanvasScale, page_view_paras.baseCanvasScale);
			drawContentOutline(category, baseRect, baseSubRectArr, baseAcceptRegion, baseCtx, 
					page_view_paras["BasePageWidth"], page_view_paras["BasePageHeight"], Base_Stroke_Color, Base_Fill_Color, baseLineWidth, basePaintOperator);
			baseCtx.restore();
			
			if (testRect.length === 0) {
				testCtx.save();
				testCtx.scale(page_view_paras.testCanvasScale, page_view_paras.testCanvasScale);
				drawContentOutline(category, baseRect, baseSubRectArr, testAcceptRegion, testCtx, 
						page_view_paras["TestPageWidth"], page_view_paras["TestPageHeight"], notFoundColor, notFoundColor, baseLineWidth, basePaintOperator);
				testCtx.restore();
			}
		}

		if (testRect.length > 0) {
			testCtx.save();
			testCtx.scale(page_view_paras.testCanvasScale, page_view_paras.testCanvasScale);
			drawContentOutline(category, testRect, testSubRectArr, testAcceptRegion, testCtx, 
					page_view_paras["TestPageWidth"], page_view_paras["TestPageHeight"], Test_Stroke_Color, Test_Fill_Color, testLineWidth, testPaintOperator);
			testCtx.restore();
			
			if (baseRect.length === 0) {
				baseCtx.save();
				baseCtx.scale(page_view_paras.baseCanvasScale, page_view_paras.baseCanvasScale);
				drawContentOutline(category, testRect, testSubRectArr, baseAcceptRegion, baseCtx, 
						page_view_paras["BasePageWidth"], page_view_paras["BasePageHeight"], notFoundColor, notFoundColor, testLineWidth, testPaintOperator);
				baseCtx.restore();
			}
		}
	};

	var drawContentOutline = function(category, outline, subOutline, acceptRegion, ctx, pageWidth, pageHeight, strokeColor, fillColor, lineWidth, paintOperator) {
		if (outline.length == 0) {
			return;
		}
		
		var t = transRect(outline, pageHeight);
		var x = t.x;
		var y = t.y;
		var h = t.h;
		var w = t.w;

		if (category === "Text") {
			var dh = parseInt(h / 4);
			y += dh;
			h += dh;
			w += 6;
		}
		
		ctx.save();
		ctx.setLineDash([4, 4]);
		ctx.lineWidth = "1";
		ctx.strokeStyle = strokeColor;
		ctx.fillStyle = fillColor;
		
		if (strokeColor === "red") {
			if (category === "Graphics") {
				ctx.rect(x, y - h, w, h);
				
				if (subOutline.length > 0) {
					ctx.save();
					ctx.setLineDash([8, 8]);
					ctx.lineWidth = 1;
					ctx.strokeStyle = "blue";
					ctx.stroke();
					ctx.restore();
					
					for (var i = 0; i < subOutline.length; i++) {
						var subRect = subOutline[i];
						var sx = toPixel(subRect[0]);
						var sy = toPixel(pageHeight - subRect[1]);
						var sh = toPixel(subRect[3]);
						var sw = toPixel(subRect[2]);
						
						ctx.save();
						ctx.lineWidth = toPixel(lineWidth);
						ctx.strokeStyle = fillColor;
						ctx.beginPath();
						ctx.moveTo(sx, sy);
						ctx.lineTo(sx + sw, sy);
						ctx.closePath();
						ctx.stroke();
						ctx.restore();
					}
				}
			} else {
				ctx.rect(x, y - h, w, h);	
				ctx.stroke();
				ctx.fillRect(x, y - h, w, h);
			}
		} else {
			ctx.save();
			ctx.lineWidth = "5";
			canvas_arrow(ctx, x - 60, y - 60, x, y);	
			ctx.restore();
		}
		
		ctx.lineWidth = "1";
		ctx.beginPath();
		ctx.moveTo(0, y + 1);
		ctx.lineTo(toPixel(pageWidth), y + 1);
		ctx.closePath();
		ctx.font = "16pt Calibri";
		ctx.strokeStyle = strokeColor;
		ctx.fillStyle = strokeColor;
		ctx.fillText("x:" + Math.round(outline[0]) + " y:" + Math.round(pageHeight - outline[1]), 4, y);
		ctx.stroke();
		ctx.restore();
		
		if (category === "Accept Difference") {
			if (acceptRegion != null && acceptRegion.length === 4) {
				var t = transRect(acceptRegion, pageHeight);
				var x = t.x;
				var y = t.y;
				var h = t.h;
				var w = t.w;
				drawAcceptDiffRegion(ctx, x, y - h, w, h);
			}
		}
	};

	var canvas_arrow = function(context, fromx, fromy, tox, toy) {
	    var headlen = 40;   // length of head in pixels
	    var angle = Math.atan2(toy - fromy, tox - fromx);
	    
	    context.beginPath();
	    context.moveTo(fromx, fromy);
	    context.lineTo(tox, toy);
	    context.lineTo(tox - headlen * Math.cos(angle - Math.PI/6),toy - headlen * Math.sin(angle - Math.PI / 6));
	    context.moveTo(tox, toy);
	    context.lineTo(tox - headlen * Math.cos(angle + Math.PI / 6),toy - headlen * Math.sin(angle + Math.PI / 6));
	    context.closePath();
	    context.stroke();
	};

	var toPixel = function (pt) {
		return parseInt((pt / 72.0) * dpi);
	};
	
	var toPoint = function (px) {
		return (px / dpi) * 72;
	};
	
	var roundMill = function(val) {
		return (Math.round(val * 1000)) / 1000;
	};
	
	var transRect = function(rect, pageHeight) {
		return {
			x : toPixel(rect[0]),
			y : toPixel(pageHeight - rect[1]),
			w : toPixel(rect[2]),
			h : toPixel(rect[3])			
		};
	};

	var cumulativeOffset = function(element) {
	    var top = 0, left = 0;
	    do {
	        top += element.offsetTop  || 0;
	        left += element.offsetLeft || 0;
	        element = element.offsetParent;
	    } while(element);

	    return {
	        top: top,
	        left: left
	    };
	};
};

PDF_DIFF.view = new PDF_DIFF.diff_report_view(PDF_DIFF.diff_report_data);

