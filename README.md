:bird: 課題提出 (@haya14busa) :bird:
====================================

### :paperclip: 課題を行った自分のGitHubのURL :paperclip:
- https://github.com/haya14busa/Hatena-Intern-Exercise2015
- 課題 JS-4: http://haya14busa.github.io/Hatena-Intern-Exercise2015/js/js-4/

### :tada: 課題に関して工夫点 :tada:

#### Scala
- invalidなLTSVをパースする際に最初のエラーで止まらずに，[scalaz](https://github.com/scalaz/scalaz) の `Validation` を使用して全てのエラーを収集して返すようにした

#### JavaScript
- 副作用のある関数(DOMに書き出すとか)と純粋な関数を分けて実装するよう意識した
- 課題4 の検索クエリをLTSVのタブをスペースにしたバージョン(LSSV: Label Space Separeted Value) 形式で柔軟に検索できるようにした (`user:frank status:200` など)
  - GmailとかGitHubの検索みたいな感じ

### :bow: 工夫したかったけど~~他のことしてて時間が足りず~~学業を優先してできなかった点 :bow:

#### Scala
- LTSVパーサ のパースメソッドの返り値型が今のところ scalaz の `Validation` 型か，エラーがあれば`throw`しちゃって返り値型は`Log`にしてあるが，ライブラリlikeに使うとすればscalazの`Validation`型で返すのは良くないハズなので標準の`Either` 型あたりに変更したい．
- 現在のLTSVパーサは完全に`Log`クラスありきだけど，任意の`case class`にマッピングしてパースできるようにしたい．
  - おそらく playframework の JSON からクラスにマッピングするときに使う `Reads converters` みたいなものを用意する必要がありそう
- 可視化の課題が1つ実装しただけかつ創意工夫点がなさすぎる... :bow:
  - 工夫点があるとすれば `Log`クラスに依存しないグラフを作成するオブジェクトを別に作成した程度

#### JavaScript
- LSSV形式のクエリだとスペースが使えない．よって `req:GET /bookmark` は使えないし， `req:GET req:bookmark` としても後ろの`bookmark`で上書きしてしまう．
- ECMAScript 2015 使いたかったけどtranspileして〜とかが既存のテストと合わせながらとかちょっと面倒そうでやめてしまった．
- グローバル変数の汚染とかは今回の課題では気にせずにやった．

### :date: 課題にかかった大体の日数 :date:
- コミットログ的には4日程度
  - Scala: 3日+α
  - JavaScript: 1日 

Hatena-Intern-Exercise
========================================

基本的な教材は Hatena::Textbook など

- [Perl 課題](./perl/)
- [Scala 課題](./scala/)
- [JavaScript 課題](./js/)

## はじめに

※この項は全課題共通になります。はじめに目を通しておきましょう。

LTSV (Labeled Tab-separated Values) とはラベル付きのTSVフォーマットです。
LTSVの1レコードは、`label:value` という形式で表されたラベル付きの値がタブ文字区切りで並びます。

以下に LTSV の例を示します。

* `sample_data/ltsv.log`

```
host:127.0.0.1	user:frank	epoch:1372694390	req:GET /apache_pb.gif HTTP/1.0	status:200	size:2326	referer:http://www.hatena.ne.jp/
host:127.0.0.1	user:john	epoch:1372794390	req:GET /apache_pb.gif HTTP/1.0	status:200	size:1234	referer:http://b.hatena.ne.jp/hotentry
host:127.0.0.1	user:-	epoch:1372894390	req:GET /apache_pb.gif HTTP/1.0	status:503	size:9999	referer:http://www.example.com/start.html
host:127.0.0.1	user:frank	epoch:1372694390	req:GET /apache_pb.gif HTTP/1.0	status:500	size:2326	referer:http://www.hatena.ne.jp/
host:127.0.0.1	user:frank	epoch:1372794395	req:GET /notfound.gif HTTP/1.0	status:404	size:100  referer:-
```

例えば、1レコード目の host の値は 127.0.0.1 であり、2レコード目の referer の値は http://b.hatena.ne.jp/hotentry になります。LTSV についてより詳しくは、以下を参照して下さい。

* http://blog.stanaka.org/entry/2013/02/05/214833
* http://ltsv.org/

## 課題の提出方法について

課題の提出は、このリポジトリをForkしてそこにコミットしていってください。

課題はそれぞれ複数問あるので、問題ごとにコミットを分けてください（すべての回答を一つのコミットにまとめないようにお願いします）。コミットの粒度は1問1コミットでなくても、細かくコミットしていて構いません。
