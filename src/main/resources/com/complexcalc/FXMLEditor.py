import re

with open("C:\\Users\\schüler\\Desktop\\Mein Ordner\\Code\\Java\\complex-calculator\\src\\main\\resources\\com\\complexcalc\\MainScene.fxml", "r", encoding="utf-8") as f:
    content = f.read()

LATEX_MAP = {
    # trigBtn and funcBtn intentionally omitted
    "absBtn":                    r"e@left|{}e@right|",
    "aBtn":                      r"a",
    "alphaBtn":                  r"e@alpha ",
    "arccosBtn":                 r"e@arccos",
    "arccoshBtn":                r"e@arccosh",
    "arccotBtn":                 r"e@arccot",
    "arccothBtn":                r"e@arccoth",
    "arccscBtn":                 r"e@arccsc",
    "arccschBtn":                r"e@arccsch",
    "arcsecBtn":                 r"e@arcsec",
    "arcsechBtn":                r"e@arcsech",
    "arcsinBtn":                 r"e@arcsin",
    "arcsinhBtn":                r"e@arcsinh",
    "arctanBtn":                 r"e@arctan",
    "arctanhBtn":                r"e@arctanh",
    "bBtn":                      r"b",
    "cbrtBtn":                   r"e@sqrt\[3\]{}",
    "cBtn":                      r"c",
    "contourIntegralBtn":        r"e@oint_{}^{}{}",
    "cosBtn":                    r"e@cos{}",
    "coshBtn":                   r"e@arccos",
    "cotBtn":                    r"e@cot",
    "cothBtn":                   r"e@coth",
    "cscBtn":                    r"e@csc",
    "cschBtn":                   r"e@csch",
    "dBtn":                      r"d",
    "definiteIntegralBtn":       r"e@int_{}^{}{}",
    "derivBtn":                  r"e@frac{d}{dx}",
    "divBtn":                    r"e@frac{}{}",
    "doubleIntegralBtn":         r"e@iint_{}^{}{}",
    "eightBtn":                  r"8",
    "equalsBtn":                 r"=",
    "factorialBtn":              r"!",
    "fBtn":                      r"f",
    "fiveBtn":                   r"5",
    "fourBtn":                   r"4",
    "gBtn":                      r"g",
    "hBtn":                      r"h",
    "iBtn":                      r"i",
    "inftyBtn":                  r"e@infty ",
    "integralBtn":               r"e@int{}",
    "integralInftyBtn":          r"e@int_{0}^{@einfty}{}",
    "jBtn":                      r"j",
    "kBtn":                      r"k",
    "lParenBtn":                 r"e@left(",
    "lBraceBtn":                 r"e@left{",
    "rParenBtn":                 r"e@right(",
    "rBraceBtn":                 r"e@right{",
    "lBracketBtn":               r"e@left\[",
    "lnBtn":                     r"e@ln{}",
    "log10Btn":                  r"e@log_{10}{}",
    "log2Btn":                   r"e@log_{2}{}",
    "logBtn":                    r"e@log_{}{}",
    "minusBtn":                  r"-",
    "mpBtn":                     r"@emp ",
    "nBtn":                      r"n",
    "nineBtn":                   r"9",
    "nRootBtn"                   r"e@sqrt[]{}"
    "oneBtn":                    r"1",
    "partialDerivBtn":           r"e@frac{@epartial}{@epartial x}",
    "partialDerivSymbolBtn":     r"@epartial ",
    "piBtn":                     r"e@pi ",
    "plusBtn":                   r"+",
    "pmBtn":                     r"@epm ",
    "pointBtn":                  r".",
    "powBtn":                    r"^{}",
    "prodBtn":                   r"e@prod_{}^{}",
    "rParenBtn":                 r"e@right)",
    "realBtn":                   r"e@mathbb{R}",
    "secBtn":                    r"e@sec",
    "sechBtn":                   r"e@sech",
    "sevenBtn":                  r"7",
    "sinBtn":                    r"e@sin",
    "sinhBtn":                   r"e@arcsin",
    "sqrtBtn":                   r"e@sqrt{}",
    "squareBtn":                 r"^{2}",
    "cubeBtn":                   r"^{3}",
    "subscriptBtn":              r"_{}",
    "sumBtn":                    r"e@sum_{}^{}",
    "tanBtn":                    r"e@tan",
    "tanhBtn":                   r"e@arctan",
    "tauBtn":                    r"τ",
    "tBtn":                      r"t",
    "threeBtn":                  r"3",
    "timesBtn":                  r"e@times ",
    "twoBtn":                    r"2",
    "vectorBtn":                 r"e@vec{}",
    "xBtn":                      r"x",
    "yBtn":                      r"y",
    "zBtn":                      r"z",
    "zeroBtn":                   r"0",
    "fancyNBtn":                 r"@emathbb{N}",
    "fancyZBtn":                 r"@emathbb{Z}",
    "fancyQBtn":                 r"@emathbb{Q}",
    "fancyRBtn":                 r"@emathbb{R}",
    "fancyCBtn":                 r"@emathbb{C}",
    "fancyHBtn":                 r"@emathbb{H}",
    "fancyOBtn":                 r"@emathbb{O}",
    "fancyDBtn":                 r"@emathbb{D}",
    "elementOfBtn":              r"@ein ",
    "notElementOfBtn":           r"@enotin ",
    "smallerThanBtn":            r"@s",
    "largerThanBtn":             r"@l",
    "largerThanEqualBtn":        r"@egeq ",
    "smallerThanEqualBtn":       r"@eleq ",
    "notEqualBtn":               r"@eneq ",
    "notBtn":                    r"@enot ",
    "normalApproxBtn":           r"@approx ",
    "equalsBtn2":                r"=",
    "equivalentBtn":             r"@eequiv ",
    "defBtn":                    r"@eoverset{@eunderset{@emathrm{def}}{}}{=}",
    "floorBtn":                  r"@eleft @elfloor {} @eright @erfloor ",
    "ceilBtn":                   r"@eleft @elceil {} @eright @erceil ",
    # trig menu items
    # func menu items — add yours here
}

def inject_userdata(match):
    tag = match.group(0)
    fxid = re.search(r'fx:id="(\w+)"', tag)
    if not fxid:
        return tag
    bid = fxid.group(1)
    if bid not in LATEX_MAP:
        return tag
    if 'userData=' in tag:
        return tag
    latex = LATEX_MAP[bid]
    return tag.replace(f'fx:id="{bid}"', f'fx:id="{bid}" userData="{latex}"')

content = re.sub(
    r'<(?:Button|SplitMenuButton|MenuItem|CheckMenuItem|RadioMenuItem)\b[^>]*(?:/>|>)',
    inject_userdata,
    content,
    flags=re.DOTALL
)

with open("C:\\Users\\schüler\\Desktop\\Mein Ordner\\Code\\Java\\complex-calculator\\src\\main\\resources\\com\\complexcalc\\MainScene.fxml", "w", encoding="utf-8") as f:
    f.write(content)

print("Done.")