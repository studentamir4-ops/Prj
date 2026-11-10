import os
import sys
import re
from http.server import HTTPServer, BaseHTTPRequestHandler
import urllib.parse

PORT = 3000
RELEASES_DIR = "/app/applet/releases"
APK_FILE = os.path.join(RELEASES_DIR, "PlumbingAccounting-v1.0.2.apk")
ZIP_FILE = os.path.join(RELEASES_DIR, "PlumbingAccounting-v1.0.2-apk.zip")
BIN_FILE = os.path.join(RELEASES_DIR, "PlumbingAccounting-v1.0.2.bin")

HTML_PAGE = """<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>دانلود برنامه حسابداری لوله کشی</title>
    <style>
        body {
            font-family: system-ui, -apple-system, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            background: linear-gradient(135deg, #0B132B, #1C2541, #111D4A);
            color: #F8FAFC;
            min-height: 100vh;
            margin: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            box-sizing: border-box;
        }
        .card {
            background: rgba(255, 255, 255, 0.08);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid rgba(255, 255, 255, 0.15);
            border-radius: 24px;
            padding: 32px;
            max-width: 500px;
            width: 100%;
            text-align: center;
            box-shadow: 0 16px 40px rgba(0, 0, 0, 0.4);
        }
        h1 {
            font-size: 24px;
            margin-bottom: 8px;
            color: #48CAE4;
        }
        p {
            font-size: 14px;
            color: #94A3B8;
            margin-bottom: 24px;
            line-height: 1.6;
        }
        .btn {
            display: block;
            width: 100%;
            padding: 16px 20px;
            margin-bottom: 12px;
            background: linear-gradient(90deg, #0077B6, #0096C7);
            color: white;
            text-decoration: none;
            border-radius: 14px;
            font-size: 16px;
            font-weight: bold;
            box-sizing: border-box;
            transition: transform 0.2s, opacity 0.2s;
        }
        .btn:hover {
            opacity: 0.95;
            transform: translateY(-2px);
        }
        .btn-secondary {
            background: rgba(255, 255, 255, 0.12);
            border: 1px solid rgba(255, 255, 255, 0.2);
            color: #E2E8F0;
            font-size: 14px;
        }
        .badge {
            display: inline-block;
            background: rgba(16, 185, 129, 0.2);
            color: #10B981;
            padding: 6px 14px;
            border-radius: 10px;
            font-size: 13px;
            margin-bottom: 16px;
            border: 1px solid rgba(16, 185, 129, 0.3);
            font-weight: bold;
        }
        .info-box {
            background: rgba(0, 0, 0, 0.25);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 14px;
            padding: 16px;
            font-size: 13px;
            color: #CBD5E1;
            margin-top: 20px;
            text-align: right;
            line-height: 1.8;
        }
    </style>
</head>
<body>
    <div class="card">
        <div class="badge">نسخه ۱.۰.۲ (حسابداری لوله کشی)</div>
        <h1>حسابداری لوله کشی</h1>
        <p>نرم‌افزار جامع مدیریت پروژه‌ها، کارفرمایان و دریافتی‌ها با طراحی شیشه‌ای Glassmorphism، پالت‌های رنگی متنوع و پشتیبان‌گیری کامل</p>
        
        <a href="/download-apk" class="btn">
            📥 دانلود مستقیم فایل APK (۲۳ مگابایت)
        </a>

        <a href="/download-zip" class="btn btn-secondary">
            📦 دانلود در قالب فایل فشرده ZIP (۲۱ مگابایت)
        </a>

        <div class="info-box">
            <b>راهنمای نصب:</b><br>
            ۱. پس از کلیک روی دکمه دانلود، حتماً منتظر بمانید تا حجم کامل <b>۲۳ مگابایت</b> به صورت ۱۰۰٪ دانلود شود.<br>
            ۲. در صورت دانلود فایل ZIP، با برنامه فایل‌های شخصی (My Files) گوشی آن را <b>Extract</b> کنید و فایل APK داخل آن را نصب نمایید.<br>
            ۳. این فایل به صورت کامل با کلید اختصاصی اندروید امضا شده است.
        </div>
    </div>
</body>
</html>
"""

class DownloadHandler(BaseHTTPRequestHandler):
    def resolve_target(self):
        url = urllib.parse.urlparse(self.path).path
        if url == "/download-zip" or url.endswith(".zip"):
            return ZIP_FILE, "application/zip", "PlumbingAccounting-v1.0.2-apk.zip"
        elif url == "/download-bin" or url.endswith(".bin"):
            return BIN_FILE, "application/octet-stream", "PlumbingAccounting-v1.0.2.bin"
        elif url == "/download-apk" or url.endswith(".apk") or url.endswith(".ap_") or "PlumbingAccounting" in url:
            return APK_FILE, "application/vnd.android.package-archive", "PlumbingAccounting-v1.0.2.apk"
        return None, None, None

    def do_HEAD(self):
        filepath, content_type, filename = self.resolve_target()
        if not filepath:
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.send_header("Content-Length", str(len(HTML_PAGE.encode("utf-8"))))
            self.end_headers()
            return

        if not os.path.exists(filepath):
            self.send_response(404)
            self.end_headers()
            return

        size = os.path.getsize(filepath)
        self.send_response(200)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(size))
        self.send_header("Accept-Ranges", "bytes")
        self.send_header("Content-Disposition", f'attachment; filename="{filename}"')
        self.send_header("Cache-Control", "no-cache")
        self.end_headers()

    def do_GET(self):
        filepath, content_type, filename = self.resolve_target()
        if not filepath:
            body = HTML_PAGE.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.send_header("Content-Length", str(len(body)))
            self.end_headers()
            self.wfile.write(body)
            return

        if not os.path.exists(filepath):
            self.send_response(404)
            self.end_headers()
            self.wfile.write(b"File not found")
            return

        file_size = os.path.getsize(filepath)
        range_header = self.headers.get("Range")

        start = 0
        end = file_size - 1

        if range_header:
            m = re.match(r"bytes=(\d+)-(\d*)", range_header)
            if m:
                start = int(m.group(1))
                if m.group(2):
                    end = int(m.group(2))

        if start >= file_size or end >= file_size or start > end:
            self.send_response(416)
            self.send_header("Content-Range", f"bytes */{file_size}")
            self.end_headers()
            return

        content_length = (end - start) + 1
        status_code = 206 if range_header else 200

        self.send_response(status_code)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(content_length))
        self.send_header("Accept-Ranges", "bytes")
        self.send_header("Content-Disposition", f'attachment; filename="{filename}"')
        self.send_header("Cache-Control", "no-cache")

        if range_header:
            self.send_header("Content-Range", f"bytes {start}-{end}/{file_size}")

        self.end_headers()

        try:
            with open(filepath, "rb") as f:
                f.seek(start)
                remaining = content_length
                chunk_size = 131072  # 128 KB
                while remaining > 0:
                    read_len = min(chunk_size, remaining)
                    data = f.read(read_len)
                    if not data:
                        break
                    self.wfile.write(data)
                    remaining -= len(data)
        except (ConnectionResetError, BrokenPipeError):
            pass

if __name__ == "__main__":
    server = HTTPServer(("0.0.0.0", PORT), DownloadHandler)
    server.serve_forever()
