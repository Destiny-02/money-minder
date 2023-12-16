import { Group, Text } from "@mantine/core";
import { Filter } from "../util/types";
import { queryToParts } from "../util/typesUtil";

type ColouredText = {
  text: string;
  type: "operator" | "string" | "contains" | "equals" | "other";
};

const getColourFromType = (type: ColouredText["type"]) => {
  switch (type) {
    case "operator":
      return "blue";
    case "string":
      return "orange";
    case "contains":
      return "green";
    case "equals":
      return "green";
    default:
      return "gray";
  }
};

const stringListToColouredText = (stringList: string[]): ColouredText[] => {
  return stringList.map((string) => {
    if (string === "contains") {
      return { text: string.toUpperCase(), type: "contains" };
    } else if (string === "equals") {
      return { text: string.toUpperCase(), type: "equals" };
    } else if (string === "and" || string === "or") {
      return { text: string.toUpperCase(), type: "operator" };
    } else if ((string.startsWith('"') || string.startsWith("'")) && (string.endsWith('"') || string.endsWith("'"))) {
      return { text: string, type: "string" };
    } else {
      return { text: string, type: "other" };
    }
  });
};

interface Props {
  filter: Filter;
}

// A row in the filters table
const FilterRow: React.FC<Props> = ({ filter }) => {
  return (
    <>
      <tr>
        <td>
          <Text>{filter.category}</Text>
        </td>
        <td>
          <Group spacing="xs">
            {stringListToColouredText(queryToParts(filter.query)).map((colouredText, index) => (
              <Text key={index} color={getColourFromType(colouredText.type)}>
                {colouredText.text}
              </Text>
            ))}
          </Group>
        </td>
      </tr>
    </>
  );
};

export default FilterRow;
