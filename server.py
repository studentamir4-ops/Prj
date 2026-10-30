import os
import sys
from http.server import HTTPServer, BaseHTTPRequestHandler
import urllib.parse

PORT = 3000
RELEASES_DIR = "/app/applet/releases"
APK_FILE = os.path.join(RELEASES_DIR, "ProjectFinance-v1.0.0.apk")
ZIP_FILE = os.path.join(RELEASES_DIR, "ProjectFinance-v1.0.0-apk.zip")
BIN_FILE = os.path.join(RELEASES_DIR, "ProjectFinance-v1.0.0.bin")

HTML_PAGE = """<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>دانلود مستقیم برنامه مدیریت پروژه و مالی</title>
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
            max-width: 480px;
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
            padding: 4px 12px;
            border-radius: 8px;
            font-size: 13px;
            margin-bottom: 16px;
            border: 1px solid rgba(16, 185, 129, 0.3);
        }
        .info-box {
            background: rgba(0, 0, 0, 0.2);
            border-radius: 12px;
            padding: 12px;
            font-size: 13px;
            color: #94A3B8;
            margin-top: 20px;
            text-align: right;
            line-height: 1.6;
        }
    </style>
</head>
<body>
    <div class="card">
        <div class="badge">نسخه ۱.۰.۰ آماده نصب</div>
        <h1>مدیریت پروژه و مالی</h1>
        <p>نرم‌افزار جامع مدیریت پروژه‌ها، کارفرمایان و دریافتی‌ها با طراحی مدرن Glassmorphism و تقویم شمسی</p>
        
        <a href="/download-apk" class="btn">
            📥 دانلود مستقیم فایل APK (۲۲ مگابایت)
        </a>

        <a href="/download-zip" class="btn btn-secondary">
            📦 دانلود در قالب فایل ZIP
        </a>

        <div class="info-box">
            <b>راهنمای نصب در گوشی:</b><br>
            پس از دانلود، روی دکمه Open یا فایلی که دانلود شده کلیک کنید. در صورت مشاهده پیام امنیتی، گزینه Allow from this source را تایید کنید تا نصب انجام شود.
        </div>
    </div>
</body>
</html>
"""

class DownloadHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        url = urllib.parse.urlparse(self.path).path
        if url == "/download-apk" or url == "/ProjectFinance-v1.0.0.apk":
            self.serve_file(APK_FILE, "application/vnd.android.package-archive", "ProjectFinance-v1.0.0.apk")
        elif url == "/download-zip" or url == "/ProjectFinance-v1.0.0-apk.zip":
            self.serve_file(ZIP_FILE, "application/zip", "ProjectFinance-v1.0.0-apk.zip")
        elif url == "/download-bin" or url == "/ProjectFinance-v1.0.0.bin":
            self.serve_file(BIN_FILE, "application/octet-stream", "ProjectFinance-v1.0.0.bin")
        else:
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.end_headers()
            self.wfile.write(HTML_PAGE.encode("utf-8"))

    def serve_file(self, filepath, content_type, filename):
        if not os.path.exists(filepath):
            self.send_response(404)
            self.end_headers()
            self.wfile.write(b"File not found")
            return
        size = os.path.getsize(filepath)
        self.send_response(200)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(size))
        self.send_header("Content-Disposition", f'attachment; filename="{filename}"')
        self.end_headers()
        with open(filepath, "rb") as f:
            while chunk := f.read(65536):
                self.wfile.write(chunk)

if __name__ == "__main__":
    server = HTTPServer(("0.0.0.0", PORT), DownloadHandler)
    server.serve_forever()
