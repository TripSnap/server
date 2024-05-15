import SwaggerUI from "swagger-ui";
import "swagger-ui/dist/swagger-ui.css";

const spec = require("./api-docs.json");
const config = require("./swagger-config.json");

// 로컬 파일을 사용하기 때문에 요청 url 관련한 항목 제거
const urlFilter = (config) =>
  Object.entries(config)
    .filter(([key]) => !key.toLowerCase().includes("url"))
    .reduce((obj, [key, value]) => ({ ...obj, [key]: value }), {});

const ui = SwaggerUI({
  spec,
  dom_id: "#swagger",
  ...urlFilter(config),
  supportedSubmitMethods: [],
});
