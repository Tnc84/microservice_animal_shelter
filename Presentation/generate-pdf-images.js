/**
 * Generate individual slide images from Presentation.html using Puppeteer
 * 
 * Requirements:
 * 1. Install Node.js from https://nodejs.org/
 * 2. Run: npm install puppeteer
 * 3. Run: node generate-pdf-images.js
 */

const puppeteer = require('puppeteer');
const path = require('path');
const fs = require('fs');

async function generateSlideImages() {
    console.log('🚀 Starting slide image generation...');
    
    const htmlFile = path.join(__dirname, 'Presentation.html');
    const outputDir = path.join(__dirname, 'presentation-slides');
    
    // Check if HTML file exists
    if (!fs.existsSync(htmlFile)) {
        console.error('❌ Error: Presentation.html not found!');
        process.exit(1);
    }
    
    // Create output directory
    if (!fs.existsSync(outputDir)) {
        fs.mkdirSync(outputDir, { recursive: true });
    }
    
    const htmlPath = `file://${htmlFile.replace(/\\/g, '/')}`;
    
    console.log(`📄 Reading: ${htmlFile}`);
    console.log(`📁 Output directory: ${outputDir}`);
    
    try {
        // Launch browser
        console.log('🌐 Launching browser...');
        const browser = await puppeteer.launch({
            headless: true,
            args: ['--no-sandbox', '--disable-setuid-sandbox']
        });
        
        const page = await browser.newPage();
        
        // Set viewport for high-quality images
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
        
        // Get all slide elements
        const slides = await page.$$('.slide');
        console.log(`📊 Found ${slides.length} slides`);
        
        // Generate image for each slide
        for (let i = 0; i < slides.length; i++) {
            console.log(`📸 Generating image ${i + 1}/${slides.length}...`);
            
            const slide = slides[i];
            const outputFile = path.join(outputDir, `slide-${String(i + 1).padStart(3, '0')}.png`);
            
            // Scroll to slide
            await slide.scrollIntoView();
            await page.waitForTimeout(500);
            
            // Take screenshot of slide
            await slide.screenshot({
                path: outputFile,
                type: 'png',
                fullPage: false
            });
        }
        
        await browser.close();
        
        console.log('✅ All slide images generated successfully!');
        console.log(`📁 Location: ${outputDir}`);
        
    } catch (error) {
        console.error('❌ Error generating images:', error);
        process.exit(1);
    }
}

// Run the function
generateSlideImages();

