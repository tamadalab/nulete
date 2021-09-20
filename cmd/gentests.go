package cmd

import (
	_ "embed"
	"fmt"
	"io"
	"os"
	"os/exec"
	"os/user"
	"path/filepath"
	"strings"

	"github.com/spf13/cobra"
)

var (
	sourceDir   string
	testcaseDir string

	gentestsCmd = &cobra.Command{
		Use:   "gentests",
		Short: "generate tests",
		PreRun: func(cmd *cobra.Command, args []string) {
			cobra.CheckErr(gentestsCmdPreRun())
		},
		Run: func(cmd *cobra.Command, args []string) {
			cobra.CheckErr(gentestsCmdRun())
		},
	}
)

//go:embed assets/gentests.jar
var gentestsJarData []byte

//go:embed assets/TestBase.java
var testBaseJavaData []byte

/*
testcaseDirが指定されていない場合、sourceDirと同じにする。
ホームディレクトリを示すチルダがある場合は展開する。
*/
func gentestsCmdPreRun() error {
	currentUser, err := user.Current()
	if err != nil {
		return err
	}

	if strings.HasPrefix(sourceDir, "~/") {
		sourceDir = strings.Replace(sourceDir, "~", currentUser.HomeDir, 1)
	} else if absDir, err := filepath.Abs(sourceDir); err != nil {
		return err
	} else {
		sourceDir = absDir
	}

	if testcaseDir == "" {
		testcaseDir = sourceDir
	} else if strings.HasPrefix(testcaseDir, "~/") {
		testcaseDir = strings.Replace(testcaseDir, "~", currentUser.HomeDir, 1)
	} else if absDir, err := filepath.Abs(testcaseDir); err != nil {
		return err
	} else {
		testcaseDir = absDir
	}

	return nil
}

/*
tmpディレクトリを作成する。tmpはreturn時に削除される。
tmp内にgentests.jarを作る。
tmp内にsourceDir内のjavaファイルをコンパイルしたclassファイルを出力する。
tmp内にtestcaseDir内のjsonファイルをコピーする。
tmp内に入り、java -jar gentests.jarを実行する。
sourceDir内のjavaファイルをsrc/main/javaへコピーする。
生成された*Test.javaをsrc/test/javaへコピーする。
src/test/java内にTestBase.javaを作る。

tmpディレクトリをdeferで削除するため、os.Exit(cobra.CheckErr)による終了はしない。
*/
func gentestsCmdRun() error {
	firstDir, err := os.Getwd()
	if err != nil {
		return err
	}

	tmpDir := filepath.Join(firstDir, "tmp")

	if err := os.Mkdir(tmpDir, 0777); err != nil {
		return err
	}
	defer os.RemoveAll(tmpDir)

	gentestsJarPath := filepath.Join(tmpDir, "gentests.jar")

	if err := os.WriteFile(gentestsJarPath, gentestsJarData, 0644); err != nil {
		return err
	}

	find := exec.Command("find", sourceDir, "-name", "*.java")
	result, err := find.Output()
	if err != nil {
		return err
	}

	javaFilePaths := strings.Split(strings.TrimSuffix(string(result), "\n"), "\n")
	if len(javaFilePaths) == 1 && javaFilePaths[0] == "" {
		return fmt.Errorf("java file not found in %s", sourceDir)
	}

	args := append([]string{"-d", tmpDir}, javaFilePaths...)

	javac := exec.Command("javac", args...)
	if stdoutStderr, err := javac.CombinedOutput(); err != nil {
		fmt.Printf("%s", stdoutStderr)
		return err
	}

	if err := copyFiles(testcaseDir, tmpDir, func(name string) bool { return filepath.Ext(name) == ".json" }); err != nil {
		return err
	}

	if err := os.Chdir(tmpDir); err != nil {
		return err
	}

	java := exec.Command("java", "-jar", "gentests.jar")
	if stdoutStderr, err := java.CombinedOutput(); err != nil {
		fmt.Printf("%s", stdoutStderr)
		return err
	}

	mainDir := filepath.Join(firstDir, "src", "main", "java")
	testDir := filepath.Join(firstDir, "src", "test", "java")

	if err := copyFiles(sourceDir, mainDir, func(name string) bool { return filepath.Ext(name) == ".java" }); err != nil {
		return err
	}

	if err := copyFiles(tmpDir, testDir, func(name string) bool { return strings.HasSuffix(name, "Test.java") }); err != nil {
		return err
	}

	testBaseJavaPath := filepath.Join(testDir, "TestBase.java")

	if err := os.WriteFile(testBaseJavaPath, testBaseJavaData, 0644); err != nil {
		return err
	}

	return nil
}

func copyFiles(fromDirPath string, toDirPath string, filter func(string) bool) error {
	fromFiles, err := os.ReadDir(fromDirPath)
	if err != nil {
		return err
	}

	for _, fromFile := range fromFiles {
		if name := fromFile.Name(); !fromFile.IsDir() && filter(name) {
			err := overwriteCopy(filepath.Join(fromDirPath, name), filepath.Join(toDirPath, name))
			if err != nil {
				return err
			}
		}
	}

	return nil
}

func overwriteCopy(fromPath string, toPath string) error {
	fromFile, err := os.Open(fromPath)
	if err != nil {
		return err
	}
	defer fromFile.Close()

	toFile, err := os.Create(toPath)
	if err != nil {
		return err
	}
	defer toFile.Close()

	_, err = io.Copy(toFile, fromFile)
	if err != nil {
		return err
	}

	return nil
}

func init() {
	gentestsCmd.Flags().StringVar(&sourceDir, "source-dir", "", "source directory")
	gentestsCmd.Flags().StringVar(&testcaseDir, "testcase-dir", "", "testcase directory")
	gentestsCmd.MarkFlagRequired("source-dir")

	rootCmd.AddCommand(gentestsCmd)
}
