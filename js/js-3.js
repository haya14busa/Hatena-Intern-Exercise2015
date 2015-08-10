// 課題 JS-3 の実装をここに記述してください。
function showTable(parsedLtsv) {
  createLogTable(
    document.querySelector('#table-container'), // DOMの取得が高コストなので外に出すべき
    parsedLtsv
  );
}

function getParsedLtsv() {
  return parseLTSVLog(document.querySelector('#log-input').value);
}

// elm.onclick だと1つしか登録できないので `addEventListener` 使ったほうがよい
document.querySelector('#submit-button').onclick = function() {
  showTable(getParsedLtsv());
};
