var PDF_DIFF = PDF_DIFF || {};
PDF_DIFF.diff_report_data = {
    "diff_page_nums": [],
    "Test_Stroke_Color": "red",
    "Test_Fill_Color": "rgba(138, 43, 226, 0.2)",
    "page_xor_images": {"0": "0-xor.png"},
    "diff_content_json_obj": [{
        "Result": {
            "Accept_Difference": [
                {
                    "Attributes": [
                        {
                            "Value": [
                                "31219171204",
                                "#DELETE#"
                            ],
                            "Key": "Text",
                            "Equals": false
                        },
                        {
                            "Value": [
                                "x=300.0, y=13.0, width=50.0, height=14.0",
                                ""
                            ],
                            "Key": "Accept Difference Region",
                            "Equals": true
                        }
                    ],
                    "SubOutline": [
                        [],
                        []
                    ],
                    "Outline": [
                        [
                            308.848,
                            16.842,
                            24.104,
                            2.924
                        ],
                        []
                    ],
                    "AcceptDiffRegion": [
                        [
                            300,
                            13,
                            50,
                            14
                        ],
                        []
                    ]
                },
                {
                    "Attributes": [
                        {
                            "Value": [
                                "#INSERT#",
                                "70511181051"
                            ],
                            "Key": "Text",
                            "Equals": false
                        },
                        {
                            "Value": [
                                "",
                                "x=300.0, y=13.0, width=50.0, height=14.0"
                            ],
                            "Key": "Accept Difference Region",
                            "Equals": true
                        }
                    ],
                    "SubOutline": [
                        [],
                        []
                    ],
                    "Outline": [
                        [],
                        [
                            308.864,
                            16.842,
                            23.548,
                            2.924
                        ]
                    ],
                    "AcceptDiffRegion": [
                        [],
                        [
                            300,
                            13,
                            50,
                            14
                        ]
                    ]
                }
            ],
            "Text": [],
            "Page": [],
            "Image": [],
            "Annot": [],
            "Path": []
        },
        "PageNo": 0
    }],
    "test_pdf_json_obj": {
        "pageCount": 1,
        "title": "test.pdf",
        "pages": [{
            "num": 0,
            "imageTag": "test-0.png",
            "height": 841,
            "width": 595
        }]
    },
    "Base_Stroke_Color": "red",
    "base_pdf_json_obj": {
        "pageCount": 1,
        "title": "control.pdf",
        "pages": [{
            "num": 0,
            "imageTag": "base-0.png",
            "height": 841,
            "width": 595
        }]
    },
    "Base_Fill_Color": "rgba(138, 43, 226, 0.2)",
    "diff_page_count": 0,
    "Rendering_Resolution": 96,
    "ShowDifferentPagesOnly" : false,
    "diff_accepted_page_nums": [0]
}