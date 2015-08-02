// 課題 JS-3 の実装をここに記述してください。
(function() {
  function showTable() {
    var ltsv = document.querySelector('#log-input').value;
    createLogTable(
      document.querySelector('#table-container'),
      parseLTSVLog(ltsv)
    );
  }
  document.querySelector('#submit-button').onclick = showTable;
})();
