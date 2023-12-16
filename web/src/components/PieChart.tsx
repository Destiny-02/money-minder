import { PieEntry, PieType, SummaryItem } from "../util/types";
import { summaryItemsToCategoriesPieEntries, summaryItemsToTypesPieEntries, makeNonNegative } from "../util/typesUtil";
import { ResponsivePie } from "@nivo/pie";
import { interpolateColors } from "../util/colorUtil";
import { useMediaQuery } from "@mantine/hooks";
import { useMantineColorScheme, useMantineTheme } from "@mantine/core";

interface Props {
  summaryItems: SummaryItem[];
  checked: boolean;
  pieType: PieType;
  divideBy: number;
}

const PieChart: React.FC<Props> = ({ summaryItems, checked, pieType, divideBy }) => {
  const pieData: PieEntry[] =
    pieType == "Categories"
      ? makeNonNegative(summaryItemsToCategoriesPieEntries(summaryItems, checked, divideBy))
      : makeNonNegative(summaryItemsToTypesPieEntries(summaryItems, checked, divideBy));

  // Colors
  const theme = useMantineTheme();
  const colors = interpolateColors("#006B5D", "#E2DFFF", pieData.length);
  const { colorScheme } = useMantineColorScheme();
  const dark = colorScheme === "dark";
  const textColor = theme.colors[dark ? "dark" : "gray"][dark ? 0 : 9];
  const backgroundColor = dark ? theme.colors["dark"][6] : "#ffffff";

  // Dimensions
  const isMobile = useMediaQuery("(max-width: 62em)"); // md
  const pieWidth = isMobile ? 250 : 400;
  const legendWidth = 150;
  const pieLegendGap = isMobile ? 25 : 50;
  let mt = 50;
  let mb = mt;
  let ml = 115;
  let mr = ml + legendWidth;
  let width = pieWidth + ml + mr + pieLegendGap;
  let height = pieWidth + mt + mb;

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
      <ResponsivePie
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
        activeOuterRadiusOffset={smallScreen ? 0 : 8}
        animate
        data={pieData}
        valueFormat={(value) =>
          // format the value as a currency e.g. $1.23
          `$${value.toFixed(2)}`
        }
        margin={{ top: mt, right: mr, bottom: mb, left: ml }}
        arcLinkLabelsSkipAngle={10}
        enableArcLinkLabels={!smallScreen}
        arcLabelsSkipAngle={20}
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

export default PieChart;
