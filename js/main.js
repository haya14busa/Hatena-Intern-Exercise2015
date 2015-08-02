// 課題 JS-1: 関数 `parseLTSVLog` を記述してください

function parseLTSVLog(ltsv_lines) {
  var parsed = parseLtsvLines(ltsv_lines);
  return parsed.map(function(ltsv) {
    if ('epoch' in ltsv) {
      ltsv.epoch = Number(ltsv.epoch);
    }
    return ltsv;
  });
}

function parseLtsvLine(ltsv) {
  var obj = {};
  var lvs = ltsv.split('\t');
  for (var i=0, len=lvs.length; i < len; ++i) {
    var lv = lvs[i];
    var lv_a = lv.split(':');
    var label = lv_a[0];
    var value = lv_a.slice(1).join(':');
    obj[label] = value;
  }
  return obj;
}

function parseLtsvLines(ltsv_lines) {
  return ltsv_lines.split('\n')
    .filter(function(line) { return line !== '' })
    .map(parseLtsvLine)
}


// 課題 JS-2: 関数 `createLogTable` を記述してください

// insertまでする
// target_dom: DOM
// logs: List[{ path: String, epoch: Int}]
function createLogTable(target_dom, logs) {
  removeAllChild(target_dom);
  target_dom.appendChild(createTable(logs));
}

function createTable(logs) {
  var table = document.createElement('table');
  if (logs.length < 1) {
    return table; // return empty table if logs is empty
  }
  table.appendChild(createHeader(logs[0]));
  table.appendChild(createBody(logs));
  return table;
}

function createHeader(log) {
  var thead = document.createElement('thead');
  var tr = document.createElement('tr');
  for (key in log) {
    var th = document.createElement('th');
    th.textContent = key;
    tr.appendChild(th);
  }
  thead.appendChild(tr);
  return thead;
}

function createBody(logs) {
  var tbody = document.createElement('tbody');
  for (var i=0, len=logs.length; i < len; ++i) {
    var log = logs[i];
    var tr = document.createElement('tr');
    // XXX: the oreder of key is not specified...?
    for (key in log) {
      var td = document.createElement('td');
      td.textContent = log[key];
      tr.appendChild(td);
    }
    tbody.appendChild(tr);
  }
  return tbody;
}

function removeAllChild(dom) {
  var fc = dom.firstChild;
  while (fc) {
    dom.removeChild(fc);
    fc = dom.firstChild;
  }
}
