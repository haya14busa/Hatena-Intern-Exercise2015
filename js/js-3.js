// 課題 JS-3 の実装をここに記述してください。
function showTable(parsedLtsv) {
  createLogTable(
    document.querySelector('#table-container'),
    parsedLtsv
  );
}

function getParsedLtsv() {
  return parseLTSVLog(document.querySelector('#log-input').value);
}

document.querySelector('#submit-button').onclick = function() {
  showTable(getParsedLtsv());
};
