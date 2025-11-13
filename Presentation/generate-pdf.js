/**
 * Generate PDF from Presentation.html using Puppeteer
 * 
 * Requirements:
 * 1. Install Node.js from https://nodejs.org/
 * 2. Run: npm install puppeteer
 * 3. Run: node generate-pdf.js
 */

const puppeteer = require('puppeteer');
const path = require('path');
const fs = require('fs');

async function generatePDF() {
    console.log('🚀 Starting PDF generation...');
    
    const htmlFile = path.join(__dirname, 'Presentation.html');
    const outputFile = path.join(__dirname, 'Presentation.pdf');
    
    // Check if HTML file exists
    if (!fs.existsSync(htmlFile)) {
        console.error('❌ Error: Presentation.html not found!');
        process.exit(1);
    }
    
    const htmlPath = `file://${htmlFile.replace(/\\/g, '/')}`;
    
    console.log(`📄 Reading: ${htmlFile}`);
    console.log(`📥 Output: ${outputFile}`);
    
    try {
        // Launch browser
        console.log('🌐 Launching browser...');
        const browser = await puppeteer.launch({
            headless: true,
            args: ['--no-sandbox', '--disable-setuid-sandbox']
        });
        
        const page = await browser.newPage();
        
        // Set viewport for better rendering
        await page.setViewport({
            width: 1920,
            height: 1080,
            deviceScaleFactor: 2
        });
        
        // Load HTML file
        console.log('📖 Loading HTML content...');
        await page.goto(htmlPath, {
            waitUntil: 'networkidle0',
            timeout: 60000
        });
        
        // Wait for content to render
        await page.waitForTimeout(2000);
        
        // Generate PDF
        console.log('📑 Generating PDF...');
        await page.pdf({
            path: outputFile,
            format: 'A4',
            printBackground: true,
            margin: {
                top: '20mm',
                right: '15mm',
                bottom: '20mm',
                left: '15mm'
            },
            preferCSSPageSize: false,
            displayHeaderFooter: false
        });
        
        await browser.close();
        
        console.log('✅ PDF generated successfully!');
        console.log(`📁 Location: ${outputFile}`);
        
    } catch (error) {
        console.error('❌ Error generating PDF:', error);
        process.exit(1);
    }
}

// Run the function
generatePDF();

