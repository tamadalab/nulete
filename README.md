# nulete

# 背景
学生がプログラミングを習得するうえで，実際にプログラムを作成しながら学ぶことができるプログラミング演習科目は重要であり，教員は授業の準備や学生への対応等に十分な時間をかけ，質の高い授業を行うことが望ましい．
しかし，プログラミング演習科目は多くの学生を相手に行われるものであり，授業ごとに課される練習問題の採点には膨大な時間がかかるため，教員は採点作業に追われて，授業の質を高める作業に当てる時間を確保することが難しい．
Github Classroomを含め，採点作業を自動化するツールも存在するが，採点用のテストプログラムは教員が作成しなければならない．
そこで，模範解答プログラムとテストケースから採点用のテスト生成するツール「nulete」を提案する．

# 実行例
- ディレクトリ構造を生成する例
```
$ mkdir lesson01
$ cd lesson01
$ nulete init
$ tree
.
├── build.gradle
└── src
   ├── main
   │   └── java
   └── test
       └── java
```

- テストを生成する例
```
$ nulete gentests --source-dir=../answers/ --testcase-dir=../testcases/
$ tree
.
├── build.gradle
└── src
    ├── main
    │   └── java
    └── test
        └── java
            ├── FugaTest.java
            ├── HogeTest.java
            └── PiyoTest.java
```

# 入出力仕様
```
usage: nulete [options] [command] [select-options]
OPTIONS
   -h, --help        ヘルプの表示
   -v, --version     バージョンの表示
COMMAND
   init              ディレクトリ構造の生成
   gentests          テストの生成
SELECT-OPTIONS
   --source-dir      模範解答プログラムの指定
   --testcase-dir    テストケースの指定
```
