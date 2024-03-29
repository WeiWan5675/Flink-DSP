package com.weiwan.dsp.core.shell;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: xiaozhennan
 * @date: 2021/6/1 18:01
 * @description: 由于默认CommonsCLI里 {@link DefaultParser} 不支持忽略未知选项,所以我们覆盖重写添加忽略功能
 */
public class ExtendedParser extends DefaultParser {

    private final ArrayList<String> notParsedArgs = new ArrayList<>();

    public String[] getNotParsedArgs() {
        return notParsedArgs.toArray(new String[notParsedArgs.size()]);
    }

    @Override
    public CommandLine parse(Options options, String[] arguments, boolean stopAtNonOption) throws ParseException {
        if (stopAtNonOption) {
            return parse(options, arguments);
        }
        List<String> knownArguments = new ArrayList<>();
        notParsedArgs.clear();
        boolean nextArgument = false;
        for (String arg : arguments) {
            if (options.hasOption(arg) || nextArgument) {
                knownArguments.add(arg);
            } else {
                notParsedArgs.add(arg);
            }

            nextArgument = options.hasOption(arg) && options.getOption(arg).hasArg();
        }
        return super.parse(options, knownArguments.toArray(new String[knownArguments.size()]));
    }
}
