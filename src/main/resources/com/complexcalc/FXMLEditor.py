import re

with open("C:\\Users\\schüler\\Desktop\\Mein Ordner\\Code\\Java\\complex-calculator\\src\\main\\resources\\com\\complexcalc\\MainScene.fxml", "r", encoding="utf-8") as f:
    content = f.read()

LATEX_MAP = {
    # trigBtn and funcBtn intentionally omitted
    "integralBtn":  r"e@int_{}^{}",
    "piBtn":        r"e@pi",
    "oneBtn":       r"1",
    "twoBtn":       r"2",
    "threeBtn":     r"3",
    "fourBtn":      r"4",
    "fiveBtn":      r"5",
    "sevenBtn":     r"7",
    "eightBtn":     r"8",
    "nineBtn":      r"9",
    "zeroBtn":      r"0",
    "pointBtn":     r".",
    "plusBtn":      r"+",
    "minusBtn":     r"-",
    "timesBtn":     r"e@times",
    "divBtn":       r"e@frac{}{}",
    "powBtn":       r"^{}",
    "squareBtn":    r"^{2}",
    "sqrtBtn":      r"e@sqrt{}",
    "logBtn":       r"e@log_{}{}",
    "lnBtn":        r"e@ln",
    "absBtn":       r"e@left|{}e@right|",
    "factorialBtn": r"!",
    "sumBtn":       r"e@sum_{}^{}",
    "prodBtn":      r"e@prod_{}^{}",
    "derivBtn":     r"e@frac{d}{dx}",
    "inftyBtn":     r"e@infty",
    "alphaBtn":     r"e@alpha",
    "vectorBtn":    r"e@vec{}",
    "realBtn":      r"e@mathbb{R}",
    "iBtn":         r"i",
    "lBracketBtn":  r"e@left(",
    "rBracketBtn":  r"e@right)",
    "equalsBtn":    r"=",
    # trig menu items
    "sinItem":      r"e@sin",
    "cosItem":      r"e@cos",
    "tanItem":      r"e@tan",
    "arcsinItem":   r"e@arcsin",
    "arccosItem":   r"e@arccos",
    "arctanItem":   r"e@arctan",
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