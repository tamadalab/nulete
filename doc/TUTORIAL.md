# チュートリアル
### nuleteのインストール
1. Git, Go, Javaをインストールしておく。
2. 以下のコマンドでnuleteをインストールする。
    * `git clone https://github.com/tamadalab/nulete.git`
        * または`git clone git@github.com:tamadalab/nulete.git`
    * `cd nulete`
    * `make install`

### Githubアカウントを取得する
1. [Githubのアカウント作成ページ](https://github.com/join)にアクセスする。
2. ユーザーネームとメールアドレスとパスワードを入力し、「Create account」をクリックする。

<img src="images/github.com_join.png" width="320px">

3. メールアドレスに届いたメールから、メールアドレスの確認をする。

<img src="images/verify_address_email.png" width="320px">
<img src="images/verify_address_github.png" width="320px">

### Github Classroomと連携する
1. [Github Classroomのページ](https://classroom.github.com/)にアクセスし、右上の「Sign in」をクリックしてGithubのアカウントで入る。

<img src="images/github_classroom.png" width="320px">

2. 「Authorize Github Classroom」をクリックして連携する。

<img src="images/github_classroom_auth.png" width="320px">

### organizationを作成する
1. Githubのページにアクセスし、右上の「+」から「New organization」をクリックする。

<img src="images/new_organization.png" width="320px">

2. 「Create a free organization」をクリックする。

<img src="images/new_organization_plan.png" width="320px">

3. アカウントネームとメールアドレスを入力し、所属を選択して、「Next」をクリックする。

<img src="images/new_organization_setup.png" width="320px">

4. 「Complete setup」をクリックする。

<img src="images/new_organization_complete.png" width="320px">

### classroomを作成する
1. [Classroomのページ](https://classroom.github.com/classrooms)で「Create your first classroom」(既にclassroomがある場合は「New classroom」)をクリックする。

<img src="images/create_classroom_first.png" width="320px">
<img src="images/create_classroom_exist.png" width="320px">

2. organizationを選択する。

<img src="images/select_organization.png" width="320px">

3. classroomの名前を入力し、「Create classroom」をクリックする。

<img src="images/create_classroom.png" width="320px">

4. 適宜TAの招待や学生の追加をし、「Continue」をクリックする。

<img src="images/inviteTA.png" width="320px">
<img src="images/add_student.png" width="320px">

### テンプレートリポジトリを作成する
1. [リポジトリ作成ページ](https://github.com/new)にアクセスする。
2. 「Owner」は作成したorganizationを選択し、「Add a README file」にチェックを入れ、その他リポジトリ名などは適当に入力する。
3. 「Create repository」をクリックする。

<img src="images/new_repository.png" width="320px">

4. リポジトリのページに遷移するので、「Setting」をクリックし、「Template repository」にチェックを入れる。

<img src="images/repository_top.png" width="320px">
<img src="images/check_template.png" width="320px">

### 模範解答プログラムとテストケースを作成する
1. 模範解答となるJavaのプログラムを作成する。

例 FizzBuzz.java
```java
public class FizzBuzz{
    void run(String[] args){
        Integer to = 15;
        if(args.length != 0){
            to = new Integer(args[0]);
        }

        for(Integer i = 1; i <= to; i++){
            if(i % 3 == 0 && i % 5 == 0){
                System.out.println("FizzBuzz");
            }
            else if(i % 3 == 0){
                System.out.println("Fizz");
            }
            else if(i % 5 == 0){
                System.out.println("Buzz");
            }
            else{
                System.out.println(i);
            }
        }
    }
    public static void main(String[] args){
        FizzBuzz fizzbuzz = new FizzBuzz();
        fizzbuzz.run(args);
    }
}
```

2. テストケースを作成する。
    * Json形式で、メソッドとテスト入力を記述する。
    * ファイル名は模範解答プログラムと同じにする。
    * argsの[]内の要素がメソッドに渡される。
    * メソッドの引数が配列の場合は、更に[]でくくれば良い。

例 FizzBuzz.json
```json
[
    {
        "method": "main",
        "args" : [
            []
        ]
    },
    {
        "method": "main",
        "args" : [
            [
                "3"
            ]
        ]
    },
    {
        "method": "run",
        "args" : [
            [
                "5"
            ]
        ]
    }
]

```

### テストの生成とGithubへのプッシュ
1. 模範解答のプログラムを1つのディレクトリにまとめる。
2. テストケースを1つのディレクトリにまとめる。(模範解答と同じディレクトリでも可)
3. 以下のコマンドでリポジトリをクローンし、テストを生成する。
    1. `git clone https://github.com/<Organization名>/<リポジトリ名>.git`
        * または `git clone git@github.com:<Organization名>/<リポジトリ名>.git`
    2. `cd <リポジトリ名>`
    3. `nulete init`
    4. `nulete gentests --source-dir <模範解答のディレクトリ> --testcase-dir <テストケースのディレクトリ>`
        * テストケースが模範解答と同じディレクトリの場合は`--testcase-dir`は不要である。
4. `src/main/java`の中に模範解答のプログラムがコピーされているので、学生に書いてもらいたい部分を削除する。
5. 以下のコマンドでコミットしてGithubへプッシュする。
    1. `git add --all`
    2. `git commit --all -m "<コミットメッセージ>"`
    3. `git push`

### assignmentを作成する
1. [Classroomのページ](https://classroom.github.com/classrooms)で作成したClassroomをクリックする。

<img src="images/classroom_top.png" width="320px">

2. 「New assignment」をクリックする。

<img src="images/created_classroom_top.png" width="320px">

3. assignment名や締切などを設定し、「Continue」をクリックする。

<img src="images/new_assignment1.png" width="320px">

4. 「Select a repository」から、作成したテンプレートリポジトリを選択し、「Continue」をクリックする。

<img src="images/new_assignment2.png" width="320px">

5. 「Enable feedback pull requests」にチェックを入れる。

<img src="images/new_assignment3.png" width="320px">

6. 「Add test」をクリックし、「Run Java」を選択する。

<img src="images/new_assignment4.png" width="320px">

7. テスト名や実行コマンド、配点などを設定し、「save test case」をクリックする。
    * プログラムが複数ある場合は、`gradle test --tests <プログラム名>Test`で各プログラム用のテストを作る。

<img src="images/new_assignment5.png" width="320px">

8. 「Create assignment」をクリックする。


### 授業のやり方
1. 招待URLを学生に送る。
2. 学生がアクセスすると、提出用のリポジトリが生成される。
3. 学生は、そのリポジトリをクローンしてプログラムを書き、Githubへプッシュすることで提出する。
4. 教員はassignmentのページから、学生は提出用のリポジトリのプルリクエストから採点結果を確認する。
