#!/usr/bin/env python3
import zipfile
import xml.etree.ElementTree as ET
import sys
import os

def read_docx(file_path):
    """读取 docx 文件内容"""
    try:
        with zipfile.ZipFile(file_path, 'r') as z:
            xml_content = z.read('word/document.xml')
            tree = ET.fromstring(xml_content)
            
            # 定义命名空间
            namespaces = {
                'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'
            }
            
            paragraphs = []
            for para in tree.iter('{http://schemas.openxmlformats.org/wordprocessingml/2006/main}p'):
                texts = []
                for text in para.iter('{http://schemas.openxmlformats.org/wordprocessingml/2006/main}t'):
                    if text.text:
                        texts.append(text.text)
                if texts:
                    paragraphs.append(''.join(texts))
            
            return '\n'.join(paragraphs)
    except Exception as e:
        return f"Error: {e}"

if __name__ == "__main__":
    # 读取任务书
    task_file = "/Users/itzhan/Desktop/毕业设计/彭青青/彭青青+任务书.docx"
    if os.path.exists(task_file):
        print("=== 任务书内容 ===")
        print(read_docx(task_file))
    
    print("\n" + "="*50 + "\n")
    
    # 读取开题报告
    report_file = "/Users/itzhan/Desktop/毕业设计/彭青青/彭青青+开题报告.docx"
    if os.path.exists(report_file):
        print("=== 开题报告内容 ===")
        print(read_docx(report_file))
