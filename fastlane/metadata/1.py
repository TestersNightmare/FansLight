import os

# 定义你的新文案模板
new_title = "Fanslight"
new_short = "极简补光手电筒，支持全屏点击变色。"
new_full = """全新的极简设计手电筒。
- 全屏点击：随机切换屏幕补光颜色。
- 极简体验：无复杂设置，打开即用。
- 隐私安全：无广告，无多余权限。
"""

metadata_path = "android"

for lang_dir in os.listdir(metadata_path):
    lang_path = os.path.join(metadata_path, lang_dir)
    if os.path.isdir(lang_path):
        # 批量更新标题
        with open(os.path.join(lang_path, "title.txt"), "w", encoding="utf-8") as f:
            f.write(new_title)
        
        # 批量更新简短描述
        with open(os.path.join(lang_path, "short_description.txt"), "w", encoding="utf-8") as f:
            f.write(new_short)
            
        # 批量更新详细描述
        with open(os.path.join(lang_path, "full_description.txt"), "w", encoding="utf-8") as f:
            f.write(new_full)

print("所有语言文件已更新！")