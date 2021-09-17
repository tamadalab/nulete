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
生成された*Test.javaをsrc/test/javaへコピーする。

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

	sourceFiles, err := os.ReadDir(sourceDir)
	if err != nil {
		return err
	}

	sourceNames := make([]string, 0)
	args := []string{"-d", tmpDir}
	for _, sourceFile := range sourceFiles {
		if name := sourceFile.Name(); filepath.Ext(name) == ".java" {
			sourceNames = append(sourceNames, name)
			args = append(args, filepath.Join(sourceDir, sourceFile.Name()))
		}
	}

	javac := exec.Command("javac", args...)
	if stdoutStderr, err := javac.CombinedOutput(); err != nil {
		fmt.Printf("%s\n", stdoutStderr)
		return err
	}

	testcaseFiles, err := os.ReadDir(testcaseDir)
	if err != nil {
		return err
	}

	for _, testcaseFile := range testcaseFiles {
		if name := testcaseFile.Name(); filepath.Ext(name) == ".json" {
			if err := overwriteCopy(filepath.Join(testcaseDir, name), filepath.Join(tmpDir, name)); err != nil {
				return err
			}
		}
	}

	if err := os.Chdir(tmpDir); err != nil {
		return err
	}

	java := exec.Command("java", "-jar", "gentests.jar")
	stdoutStderr, err := java.CombinedOutput()
	fmt.Printf("%s\n", stdoutStderr)
	if err != nil {
		return err
	}

	outputDir := filepath.Join(firstDir, "src", "test", "java")
	for _, sourceName := range sourceNames {
		testName := strings.Replace(sourceName, ".java", "Test.java", 1)
		if err := overwriteCopy(filepath.Join(tmpDir, testName), filepath.Join(outputDir, testName)); err != nil {
			return err
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
