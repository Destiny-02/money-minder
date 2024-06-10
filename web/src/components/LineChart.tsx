import { LineEntry, CategorySummary } from "../util/types";
import { categorySummariesToLineEntries } from "../util/typesUtil";
import { ResponsiveLine } from '@nivo/line'
import { interpolateColors } from "../util/colorUtil";
import { useMediaQuery } from "@mantine/hooks";
import { useMantineColorScheme, useMantineTheme } from "@mantine/core";

interface Props {
  categorySummaries: CategorySummary[];
}

const LineChart: React.FC<Props> = ({ categorySummaries }) => {
  const lineData: LineEntry[] =  categorySummariesToLineEntries(categorySummaries);

  // Colors
  const theme = useMantineTheme();
  const colors = interpolateColors("#006B5D", "#E2DFFF", lineData.length);
  const { colorScheme } = useMantineColorScheme();
  const dark = colorScheme === "dark";
  const textColor = theme.colors[dark ? "dark" : "gray"][dark ? 0 : 9];
  const backgroundColor = dark ? theme.colors["dark"][6] : "#ffffff";

  // Dimensions
  const isMobile = useMediaQuery("(max-width: 62em)"); // md
  const chartWidth = isMobile ? 250 : 400;
  const legendWidth = 150;
  const chartLegendGap = isMobile ? 25 : 50;
  let mt = 50;
  let mb = mt;
  let ml = 115;
  let mr = ml + legendWidth;
  let width = chartWidth + ml + mr + chartLegendGap;
  let height = chartWidth + mt + mb;

  // Portait narrow phone screens e.g. iPhone SE
  const smallScreen = useMediaQuery("(max-width: 48em)"); // sm
  if (smallScreen) {
    width = 250;
    height = 300;
    mt = 0;
    mb = 0;
    ml = 0;
    mr = 0;
  }

  return (
    <div style={{ height: height, width: width }}>
      <ResponsiveLine
        theme={{
          text: {
            fill: textColor,
            fontFamily: "poppins",
          },
          tooltip: {
            container: {
              color: textColor,
              background: backgroundColor,
            },
          },
        }}
        colors={colors}
        animate
        useMesh
        data={lineData}
        axisLeft={{
          format: (value) => `$${value}`,
        }}
        yFormat={
          (value) => typeof value === 'number' ? `$${value.toFixed(2)}` : value.toString()
        }
        yScale={
          {min: 'auto', max: 'auto', type: 'linear', reverse: true}
        }
        enableSlices="x"
        margin={{ top: mt, right: mr, bottom: mb, left: ml }}
        legends={
          smallScreen
            ? []
            : [
                {
                  anchor: "right",
                  direction: "column",
                  justify: false,
                  translateX: 200,
                  translateY: 0,
                  itemWidth: 100,
                  itemHeight: 18,
                  itemDirection: "left-to-right",
                  itemOpacity: 1,
                  itemsSpacing: 4,
                  symbolSize: 18,
                  symbolShape: "square",
                },
              ]
        }
      />
    </div>
  );
};

export default LineChart;
