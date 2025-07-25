#!/bin/bash

# Create directories if they don't exist
mkdir -p app/src/main/res/mipmap-{hdpi,mdpi,xhdpi,xxhdpi,xxxhdpi}

# Generate PNGs from SVG for different densities
# Note: This is a simplified version. In a real project, you'd use Android Studio's
# built-in Image Asset Studio or a proper image processing tool like Inkscape.

# Create a simple launcher icon for each density
for density in hdpi mdpi xhdpi xxhdpi xxxhdpi; do
  size=0
  case $density in
    "mdpi") size=48 ;;
    "hdpi") size=72 ;;
    "xhdpi") size=96 ;;
    "xxhdpi") size=144 ;;
    "xxxhdpi") size=192 ;;
  esac
  
  # Create a simple colored circle with the app initial
  convert -size ${size}x${size} xc:none \
    -fill '#6200EE' -draw "circle $((size/2)),$((size/2)) $((size/2)),$((size/4))" \
    -fill white -pointsize $((size/2)) -gravity center \
    -draw "text 0,0 'SB'" \
    "app/src/main/res/mipmap-${density}/ic_launcher.png"
    
  # Create round icon (same as regular for now)
  cp "app/src/main/res/mipmap-${density}/ic_launcher.png" "app/src/main/res/mipmap-${density}/ic_launcher_round.png"
done

echo "Launcher icons generated. Please check the output files and replace them with proper icons."
