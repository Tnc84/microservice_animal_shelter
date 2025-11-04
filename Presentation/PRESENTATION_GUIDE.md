# Presentation PDF/Image Generation Guide

This guide explains how to convert your `Presentation.html` file into PDF format or individual slide images.

## 📋 Quick Start

### Option 1: Automated PDF Generation (Recommended)

**Prerequisites:**
- Node.js installed (download from https://nodejs.org/)

**Steps:**
1. Install dependencies:
   ```bash
   npm install
   ```
   Or just run:
   ```bash
   generate-pdf.bat
   ```
   (This will automatically install Puppeteer if needed)

2. Generate PDF:
   ```bash
   generate-pdf.bat
   ```
   Or:
   ```bash
   npm run generate-pdf
   ```

3. Find your PDF: `Presentation.pdf` in the project root

### Option 2: Generate Individual Slide Images

1. Run the image generator:
   ```bash
   generate-images.bat
   ```
   Or:
   ```bash
   npm run generate-images
   ```

2. Find your images: `presentation-slides/` directory with numbered PNG files

### Option 3: Manual Browser Method (No Installation Required)

**For PDF:**
1. Open `Presentation.html` in your browser (Chrome, Edge, or Firefox)
2. Press `Ctrl+P` (or `Cmd+P` on Mac) to open print dialog
3. Select "Save as PDF" as the destination
4. In print settings:
   - Set margins to "Minimum" or "None"
   - Enable "Background graphics"
   - Set paper size to "A4" or "Letter"
5. Click "Save" and choose location

**For Images:**
1. Open `Presentation.html` in your browser
2. Use browser developer tools (F12)
3. Scroll to each slide
4. Use browser screenshot tools or extensions
5. Or use Windows Snipping Tool (Win+Shift+S)

## 🔧 Detailed Setup

### Installing Node.js and Dependencies

1. **Download Node.js:**
   - Visit: https://nodejs.org/
   - Download the LTS version (recommended)
   - Run installer and follow instructions
   - Verify installation: Open command prompt and run `node --version`

2. **Install Puppeteer:**
   ```bash
   npm install puppeteer
   ```
   This will download Chromium (required for PDF generation)

### Advanced Usage

#### Custom PDF Settings

Edit `generate-pdf.js` to customize:
- Page size (A4, Letter, etc.)
- Margins
- Page orientation
- Image quality

#### Custom Image Settings

Edit `generate-pdf-images.js` to customize:
- Image resolution (width/height)
- Image format (PNG, JPEG)
- Image quality

## 📊 Output Formats

### PDF Output
- **File:** `Presentation.pdf`
- **Format:** A4, portrait orientation
- **Features:** 
  - All slides in one document
  - Page breaks between slides
  - Print-ready format
  - High quality

### Image Output
- **Directory:** `presentation-slides/`
- **Format:** PNG (high quality)
- **Naming:** `slide-001.png`, `slide-002.png`, etc.
- **Features:**
  - Individual images for each slide
  - High resolution (3840x2160 with 2x scale)
  - Suitable for presentations or sharing

## 🐛 Troubleshooting

### "Node.js is not installed"
- Download and install Node.js from https://nodejs.org/
- Restart your command prompt after installation

### "Puppeteer installation fails"
- Check your internet connection
- Try: `npm install puppeteer --legacy-peer-deps`
- Or use manual browser method instead

### "PDF generation takes too long"
- This is normal for large presentations
- Wait for completion (usually 1-2 minutes)
- Check if browser window appears (may be hidden)

### "Images are cut off"
- Edit `generate-pdf-images.js`
- Increase `deviceScaleFactor` or adjust viewport size

### "PDF quality is low"
- Edit `generate-pdf.js`
- Increase `deviceScaleFactor` in `setViewport`
- Adjust PDF format settings

## 📝 File Structure

```
project-root/
├── Presentation.html          # Source HTML presentation
├── generate-pdf.js            # PDF generation script
├── generate-pdf-images.js     # Image generation script
├── generate-pdf.bat          # Windows batch script for PDF
├── generate-images.bat        # Windows batch script for images
├── package.json               # Node.js dependencies
├── Presentation.pdf           # Generated PDF (after running)
└── presentation-slides/       # Generated images (after running)
    ├── slide-001.png
    ├── slide-002.png
    └── ...
```

## 🎯 Best Practices

1. **For Presentations:**
   - Use PDF format for easy sharing
   - Use images for embedding in other tools

2. **For Sharing:**
   - PDF is better for email/print
   - Images are better for social media/web

3. **For Printing:**
   - Use PDF format
   - Ensure "Background graphics" is enabled
   - Use A4 or Letter size

4. **For Web:**
   - Use individual PNG images
   - Optimize images if needed (reduce file size)

## 💡 Tips

- **Large Presentations:** PDF generation may take 1-2 minutes, be patient
- **Image Quality:** PNG format provides best quality but larger file sizes
- **Browser Method:** Fastest method, no installation needed
- **Automated Method:** Best for batch processing or CI/CD

## 🔗 Additional Resources

- [Puppeteer Documentation](https://pptr.dev/)
- [Node.js Documentation](https://nodejs.org/docs/)
- [HTML to PDF Best Practices](https://developer.mozilla.org/en-US/docs/Web/HTML/Printing)

---

**Note:** The automated scripts use Puppeteer which requires downloading Chromium (~170MB) on first run. This is a one-time download.

