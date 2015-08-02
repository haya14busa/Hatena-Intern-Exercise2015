function parseLSSV(lssv) {
  var obj = {};
  var lvs = lssv.split(' ');
  for (var i=0, len=lvs.length; i < len; ++i) {
    var lv = lvs[i];
    var lv_a = lv.split(':');
    var label = lv_a[0];
    var value = lv_a.slice(1).join(':');
    obj[label] = value;
  }
  return obj;
}

function filterLtsvsWithQuery(ltsvs, query) {
  return ltsvs.filter(function(ltsv) {
    for (q in query) {
      if (q in ltsv && ltsv[q].indexOf(query[q]) === -1) {
        return false;
      }
    }
    return true;
  });
}

var logSearch = document.querySelector('#log-search');

logSearch.oninput = function() {
  var query = parseLSSV(this.value);
  var ltsv = getParsedLtsv();
  showTable(filterLtsvsWithQuery(ltsv, query));
};

