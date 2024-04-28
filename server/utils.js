// Function to convert a JSON object to an XML string.
function convertToXML(jsonObject, rootElement) {
  let xml = '<?xml version="1.0" encoding="UTF-8"?>';
  xml += `<${rootElement}>`; // Begin the root element.

  const appendDataToXml = (data, key) => {
    // If an array is encountered, iterate through it.
    if (Array.isArray(data)) {
      for (const item of data) {
        xml += key ? `<${key}>` : "";
        appendDataToXml(item, key);
        xml += key ? `</${key}>` : "";
      }
    } else if (data instanceof Object) {
      // If an object is encountered, iterate through its keys.
      for (let key in data) {
        xml += key ? `<${key}>` : "";
        appendDataToXml(data[key], key);
        xml += key ? `</${key}>` : "";
      }
    } else {
      // Append the data as a text node.
      xml += data;
    }
  };

  // Initialize the recursive XML conversion.
  appendDataToXml(jsonObject, null);
  xml += `</${rootElement}>`;
  return xml;
}

module.exports = {
  convertToXML,
};
