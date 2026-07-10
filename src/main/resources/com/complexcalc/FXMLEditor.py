import re

with open("C:\\Users\\schüler\\Desktop\\Mein Ordner\\Code\\Java\\complex-calculator\\src\\main\\resources\\com\\complexcalc\\MainScene.fxml", "r", encoding="utf-8") as f:
    content = f.read()

LATEX_MAP = {
    # trigBtn and funcBtn intentionally omitted
    "absBtn":                    r"e@left|{}e@right|",
    "aBtn":                      r"a",
    "sixBtn":                    r"6",
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
    "cbrtBtn":                   r"e@sqrt[3]{}",
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
    "integralInftyBtn":          r"e@int_{0}^{e@infty}{}",
    "jBtn":                      r"j",
    "kBtn":                      r"k",
    "lParenBtn":                 r"e@left(",
    "lBraceBtn":                 r"e@lefte@{",
    "rParenBtn":                 r"e@right(",
    "rBraceBtn":                 r"e@righte@}",
    "lBracketBtn":               r"e@left[",
    "rBracketBtn":               r"e@right]",
    "lnBtn":                     r"e@ln{}",
    "log10Btn":                  r"e@log_{10}{}",
    "log2Btn":                   r"e@log_{2}{}",
    "logBtn":                    r"e@log_{}{}",
    "minusBtn":                  r"-",
    "mpBtn":                     r"e@mp ",
    "nBtn":                      r"n",
    "nineBtn":                   r"9",
    "nRootBtn"                   r"e@sqrt[]{}"
    "oneBtn":                    r"1",
    "partialDerivBtn":           r"e@frac{e@partial}{e@partial x}",
    "partialDerivSymbolBtn":     r"e@partial ",
    "piBtn":                     r"e@pi ",
    "plusBtn":                   r"+",
    "pmBtn":                     r"e@pm ",
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
    "timesBtn":                  r"×",
    "twoBtn":                    r"2",
    "vectorBtn":                 r"e@vec{}",
    "xBtn":                      r"x",
    "yBtn":                      r"y",
    "zBtn":                      r"z",
    "zeroBtn":                   r"0",
    "fancyNBtn":                 r"e@mathbb{N}",
    "fancyZBtn":                 r"e@mathbb{Z}",
    "fancyQBtn":                 r"e@mathbb{Q}",
    "fancyRBtn":                 r"e@mathbb{R}",
    "fancyCBtn":                 r"e@mathbb{C}",
    "fancyHBtn":                 r"e@mathbb{H}",
    "fancyOBtn":                 r"e@mathbb{O}",
    "fancyDBtn":                 r"e@mathbb{D}",
    "elementOfBtn":              r"e@in ",
    "notElementOfBtn":           r"e@notin ",
    "smallerThanBtn":            r"@s",
    "greaterThanBtn":            r"@l",
    "greaterThanEqualBtn":       r"e@geq ",
    "smallerThanEqualBtn":       r"e@leq ",
    "notEqualBtn":               r"e@neq ",
    "notBtn":                    r"e@not ",
    "approxBtn2":                r"≈",
    "equalsBtn2":                r"=",
    "equalsBtn3":                r"=",
    "equivalentBtn":             r"e@equiv ",
    "defBtn":                    r"e@overset{e@underset{e@mathrm{def}}{}}{=}",
    "floorBtn":                  r"e@left e@lfloor {} e@right e@rfloor ",
    "ceilBtn":                   r"e@left e@lceil {} e@right e@rceil ",
    # trig menu items
    # func menu items
}

def inject_userdata(match):
    tag = match.group(0)
    fxid = re.search(r'fx:id="(\w+)"', tag)
    if not fxid:
        return tag
    bid = fxid.group(1)
    if bid not in LATEX_MAP:
        return tag

    latex = LATEX_MAP[bid]
    inject_userdata.count += 1

    if 'userData=' in tag:
        return re.sub(r'userData="[^"]*"', f'userData="{latex}"', tag)
    else:
        return tag.replace(f'fx:id="{bid}"', f'fx:id="{bid}" userData="{latex}"')

inject_userdata.count = 0

content = re.sub(
    r'<(?:Button|SplitMenuButton|MenuItem|CheckMenuItem|RadioMenuItem)\b[^>]*(?:/>|>)',
    inject_userdata,
    content,
    flags=re.DOTALL
)

with open("C:\\Users\\schüler\\Desktop\\Mein Ordner\\Code\\Java\\complex-calculator\\src\\main\\resources\\com\\complexcalc\\MainScene.fxml", "w", encoding="utf-8") as f:
    f.write(content)

print(f"Done. Wrote userData to {inject_userdata.count} button(s).")