package cmd

import (
  "github.com/spf13/cobra"
)

var rootCmd = &cobra.Command{
  Use: "nulete",
  Version: "0.1.0",
  Run: func(cmd *cobra.Command, args []string) {
    cmd.Help()
  },
}

func Execute() {
  cobra.CheckErr(rootCmd.Execute())
}
