export function interpolateColors(startColor: string, endColor: string, numColors: number): string[] {
  if (numColors <= 1) {
    return [startColor];
  }

  const startRGB = hexToRGB(startColor);
  const endRGB = hexToRGB(endColor);
  const colors: string[] = [];

  for (let i = 0; i < numColors; i++) {
    const ratio = i / (numColors - 1);
    const interpolatedColor = interpolateRGB(startRGB, endRGB, ratio);
    colors.push(rgbToHex(interpolatedColor));
  }

  return colors;
}

function hexToRGB(hexColor: string): number[] {
  const hex = hexColor.replace(/^#/, "");
  const r = parseInt(hex.substr(0, 2), 16);
  const g = parseInt(hex.substr(2, 2), 16);
  const b = parseInt(hex.substr(4, 2), 16);
  return [r, g, b];
}

function interpolateRGB(startRGB: number[], endRGB: number[], ratio: number): number[] {
  const interpolatedRGB: number[] = [];
  for (let i = 0; i < 3; i++) {
    const channelValue = Math.round(startRGB[i] + (endRGB[i] - startRGB[i]) * ratio);
    interpolatedRGB.push(channelValue);
  }
  return interpolatedRGB;
}

function rgbToHex(rgb: number[]): string {
  const [r, g, b] = rgb;
  const hexR = r.toString(16).padStart(2, "0");
  const hexG = g.toString(16).padStart(2, "0");
  const hexB = b.toString(16).padStart(2, "0");
  return `#${hexR}${hexG}${hexB}`;
}
