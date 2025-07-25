import os
import base64
from pathlib import Path

# Base64 encoded 1x1 transparent PNG
TRANSPARENT_1X1_PNG = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="

def create_directories():
    """Create necessary directories for launcher icons."""
    base_dir = Path("app/src/main/res")
    for density in ["mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"]:
        (base_dir / f"mipmap-{density}").mkdir(parents=True, exist_ok=True)

def create_launcher_icons():
    """Create placeholder launcher icons."""
    # Decode the transparent 1x1 PNG
    icon_data = base64.b64decode(TRANSPARENT_1X1_PNG)
    
    # Create launcher icons in each density directory
    base_dir = Path("app/src/main/res")
    for density in ["mdpi", "hdpi", "xhdpi", "xxhdpi", "xxxhdpi"]:
        # Create regular launcher icon
        with open(base_dir / f"mipmap-{density}/ic_launcher.png", "wb") as f:
            f.write(icon_data)
        
        # Create round launcher icon (same as regular for now)
        with open(base_dir / f"mipmap-{density}/ic_launcher_round.png", "wb") as f:
            f.write(icon_data)
    
    print("Placeholder launcher icons created successfully.")
    print("Please replace them with your actual app icons.")

if __name__ == "__main__":
    create_directories()
    create_launcher_icons()
