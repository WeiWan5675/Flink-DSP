package com.weiwan.dsp.core.shell;

import org.apache.commons.cli.*;
import org.apache.flink.client.cli.CliArgsException;

import javax.annotation.Nullable;

/**
 * @Author: xiaozhennan
 * @Date: 2021/5/30 19:44
 * @Package: com.weiwan.dsp.core.runing
 * @ClassName: CommonOptionParserV2
 * @Description: 基础的参数解析器
 **/
public abstract class CommonOptionParser {


    //打印帮助信息
    public static final Option HELP_OPTION =
            new Option(
                    "h",
                    "help",
                    false,
                    "Show the help message for the CLI Frontend or the action.");


    public static final Option VERBOSE = new Option(
            "v",
            "verbose",
            false,
            "This option is deprecated.");

    static final Option DYNAMIC_PROPERTIES =
            Option.builder("D")
                    .argName("property=value")
                    .numberOfArgs(2)
                    .valueSeparator('=')
                    .desc(
                            "Allows specifying multiple generic configuration options. The available "
                                    + "options can be found at https://ci.apache.org/projects/flink/flink-docs-stable/ops/config.html")
                    .build();

    /**
     * Merges the given {@link Options} into a new Options object.
     *
     * @param optionsA options to merge, can be null if none
     * @param optionsB options to merge, can be null if none
     * @return
     */
    public static Options mergeOptions(@Nullable Options optionsA, @Nullable Options optionsB) {
        final Options resultOptions = new Options();
        if (optionsA != null) {
            for (Option option : optionsA.getOptions()) {
                resultOptions.addOption(option);
            }
        }

        if (optionsB != null) {
            for (Option option : optionsB.getOptions()) {
                resultOptions.addOption(option);
            }
        }

        return resultOptions;
    }


    // --------------------------------------------------------------------------------------------
    //  Line Parsing
    // --------------------------------------------------------------------------------------------

    public static CommandLine parse(Options options, String[] args, boolean stopAtNonOptions)
            throws CliArgsException {
        //具体使用那种解析器,取决于是否容忍未知命令
        final DefaultParser parser = new DefaultParser();
        return parse(parser, options, args, stopAtNonOptions);
    }

    public static CommandLine parse(DefaultParser parser, Options options, String[] args, boolean stopAtNonOptions)
            throws CliArgsException {
        //具体使用那种解析器,取决于是否容忍未知命令
        try {
            return parser.parse(options, args, stopAtNonOptions);
        } catch (ParseException e) {
            throw new CliArgsException(e.getMessage());
        }
    }


    public static void printHelp(){
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(120);
        System.out.println();
        System.out.println("\n Flink-DSP Support parameters");
        Options dspOptions = DspOptionParser.getDspOptions();
        helpFormatter.printHelp(" ", dspOptions);


        System.out.println();
        System.out.println("\nAction \"run\" compiles and runs a program.");
        System.out.println("\n  Syntax: run [OPTIONS] <jar-file> <arguments>");
        helpFormatter.setSyntaxPrefix("  \"run\" action options:");
        Options runCommandOptions = FlinkOptionParser.getRunCommandOptions();
        helpFormatter.printHelp(" ", runCommandOptions);


        System.out.println(
                "\nAction \"info\" shows the optimized execution plan of the program (JSON).");
        System.out.println("\n  Syntax: info [OPTIONS] <jar-file> <arguments>");
        helpFormatter.setSyntaxPrefix("  \"info\" action options:");
        Options infoCommandOptions = FlinkOptionParser.getInfoCommandOptions();
        helpFormatter.printHelp(" ", infoCommandOptions);



        System.out.println("\nAction \"list\" lists running and scheduled programs.");
        System.out.println("\n  Syntax: list [OPTIONS]");
        helpFormatter.setSyntaxPrefix("  \"list\" action options:");
        Options listCommandOptions = FlinkOptionParser.getListCommandOptions();
        helpFormatter.printHelp(" ", listCommandOptions);


        System.out.println(
                "\nAction \"stop\" stops a running program with a savepoint (streaming jobs only).");
        System.out.println("\n  Syntax: stop [OPTIONS] <Job ID>");
        helpFormatter.setSyntaxPrefix("  \"stop\" action options:");
        Options stopCommandOptions = FlinkOptionParser.getStopCommandOptions();
        helpFormatter.printHelp(" ", stopCommandOptions);

        System.out.println("\nAction \"cancel\" cancels a running program.");
        System.out.println("\n  Syntax: cancel [OPTIONS] <Job ID>");
        helpFormatter.setSyntaxPrefix("  \"cancel\" action options:");
        Options cancelCommandOptions = FlinkOptionParser.getCancelCommandOptions();
        helpFormatter.printHelp(" ", cancelCommandOptions);

        System.out.println(
                "\nAction \"savepoint\" triggers savepoints for a running job or disposes existing ones.");
        System.out.println("\n  Syntax: savepoint [OPTIONS] <Job ID> [<target directory>]");
        helpFormatter.setSyntaxPrefix("  \"savepoint\" action options:");
        Options savepointCommandOptions = FlinkOptionParser.getSavepointCommandOptions();
        helpFormatter.printHelp(" ", savepointCommandOptions);

        System.out.println();
    }


    public static Options getOptions(Options options, Option ... option) {
        for (Option o : option) {
            options.addOption(o);
        }
        return options;
    }

    public static Options getCommonOptions(){
        Options options = new Options();
        options.addOption(HELP_OPTION);
        options.addOption(DYNAMIC_PROPERTIES);
        options.addOption(VERBOSE);
        return options;
    }

}
