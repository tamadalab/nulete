package cmd

import (
	_ "embed"
	"os"
	"path/filepath"

	"github.com/spf13/cobra"
)

var initCmd = &cobra.Command{
	Use:   "init",
	Short: "make directories and generate build.gradle",
	Run: func(cmd *cobra.Command, args []string) {
		initCmdRun()
	},
}

//go:embed assets/build.gradle
var buildGradleData []byte

//go:embed assets/gitignore.txt
var gitignoreData []byte

func initCmdRun() {
	cobra.CheckErr(os.MkdirAll(filepath.Join("src", "main", "java"), 0777))
	cobra.CheckErr(os.MkdirAll(filepath.Join("src", "test", "java"), 0777))
	cobra.CheckErr(os.WriteFile("build.gradle", buildGradleData, 0644))
	cobra.CheckErr(os.WriteFile(".gitignore", gitignoreData, 0644))
}

func init() {
	rootCmd.AddCommand(initCmd)
}
