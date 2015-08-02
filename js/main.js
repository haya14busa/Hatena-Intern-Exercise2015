// 課題 JS-1: 関数 `parseLTSVLog` を記述してください

function parseLTSVLog(ltsv_lines) {
  var parsed = parseLtsvLines(ltsv_lines);
  return parsed.map(function(ltsv) {
    if ('epoch' in ltsv) {
      ltsv.epoch = Number(ltsv.epoch);
    }
    return ltsv
  });
}

function parseLtsvLine(ltsv) {
  var obj = {};
  var lvs = ltsv.split('\t');
  for (var i=0, len=lvs.length; i < len; ++i) {
    var lv = lvs[i];
    var lv_a = lv.split(':')
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
