var PDF_DIFF = PDF_DIFF || {};
PDF_DIFF.diff_report_data = {
    "Test_Stroke_Color": "red",
    "diff_page_nums": [0],
    "page_xor_images": {"0": "0-xor.png"},
    "diff_content_json_obj": [{
        "Result": {
            "Accept_Difference": [],
            "Text": [],
            "Page": [],
            "Image": [],
            "Annot": [],
            "Path": [
                {
                    "Attributes": [
                        {
                            "Value": [
                                "Stroke",
                                "Stroke"
                            ],
                            "Key": "Paint Operator",
                            "Equals": true
                        },
                        {
                            "Value": [
                                "1.0",
                                "3.0"
                            ],
                            "Key": "Line Width",
                            "Equals": false
                        },
                        {
                            "Value": [
                                "DeviceRGB",
                                "DeviceRGB"
                            ],
                            "Key": "Stroke Colorspace",
                            "Equals": true
                        },
                        {
                            "Value": [
                                "1.0 1.0 0.0 ",
                                "1.0 1.0 0.0 "
                            ],
                            "Key": "Stroke Color",
                            "Equals": true
                        },
                        {
                            "Value": [
                                "x=121.55, y=557.25, width=234.75, height=135.65",
                                "x=121.55, y=557.25, width=234.75, height=135.65"
                            ],
                            "Key": "Position & Size",
                            "Equals": true
                        }
                    ],
                    "SubOutline": [
                        [[
                            121.55,
                            557.25,
                            234.75,
                            135.65
                        ]],
                        [[
                            121.55,
                            557.25,
                            234.75,
                            135.65
                        ]]
                    ],
                    "Outline": [
                        [
                            121.55,
                            557.25,
                            234.75,
                            135.65
                        ],
                        [
                            121.55,
                            557.25,
                            234.75,
                            135.65
                        ]
                    ],
                    "AcceptDiffRegion": [
                        [],
                        []
                    ]
                },
                {
                    "Attributes": [
                        {
                            "Value": [
                                "",
                                "Stroke"
                            ],
                            "Key": "Paint Operator",
                            "Equals": false
                        },
                        {
                            "Value": [
                                "",
                                "0.48"
                            ],
                            "Key": "Line Width",
                            "Equals": false
                        },
                        {
                            "Value": [
                                "",
                                "DeviceGray"
                            ],
                            "Key": "Stroke Colorspace",
                            "Equals": false
                        },
                        {
                            "Value": [
                                "",
                                "0.0 "
                            ],
                            "Key": "Stroke Color",
                            "Equals": false
                        },
                        {
                            "Value": [
                                "",
                                "x=90.504, y=330.29, width=142.826, height=51.86"
                            ],
                            "Key": "Position & Size",
                            "Equals": false
                        }
                    ],
                    "SubOutline": [
                        [],
                        [[
                            90.504,
                            330.29,
                            142.826,
                            51.86
                        ]]
                    ],
                    "Outline": [
                        [],
                        [
                            90.504,
                            330.29,
                            142.826,
                            51.86
                        ]
                    ],
                    "AcceptDiffRegion": [
                        [],
                        []
                    ]
                }
            ]
        },
        "PageNo": 0
    }],
    "diff_page_count": 1,
    "diff_accepted_page_nums": [],
    "FileFormat": "pdf",
    "ShowDifferentPagesOnly": true,
    "Test_Fill_Color": "rgba(138, 43, 226, 0.2)",
    "test_pdf_json_obj": {
        "fileSize": "160533 Bytes",
        "author": "Li, Mike",
        "pageCount": 1,
        "title": "",
        "keywords": "",
        "location": "C:\\dev\\mine\\git\\xdiff\\src\\test\\resources\\testcases\\xdiff\\graphics\\complex\\test.pdf",
        "created": "Thu Oct 19 18:50:45 CST 2017",
        "subject": "",
        "pages": [{
            "num": 0,
            "imageTag": "test-0.png",
            "height": 792,
            "width": 612
        }],
        "producer": "Microsoft® Word 2013",
        "fileName": "test.pdf",
        "version": "1.5",
        "modified": "Thu Oct 19 18:50:45 CST 2017"
    },
    "Base_Stroke_Color": "red",
    "base_pdf_json_obj": {
        "fileSize": "160498 Bytes",
        "author": "Li, Mike",
        "pageCount": 1,
        "title": "",
        "keywords": "",
        "location": "C:\\dev\\mine\\git\\xdiff\\src\\test\\resources\\testcases\\xdiff\\graphics\\complex\\control.pdf",
        "created": "Thu Oct 19 18:43:39 CST 2017",
        "subject": "",
        "pages": [{
            "num": 0,
            "imageTag": "base-0.png",
            "height": 792,
            "width": 612
        }],
        "producer": "Microsoft® Word 2013",
        "fileName": "control.pdf",
        "version": "1.5",
        "modified": "Thu Oct 19 18:43:39 CST 2017"
    },
    "Base_Fill_Color": "rgba(138, 43, 226, 0.2)",
    "Rendering_Resolution": 96
}