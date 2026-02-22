CURSOR = "cursor"
CLICK = "click"


class Actions:
    """MCSTP-specific action message constructors."""

    @staticmethod
    def cursor(x: float, y: float) -> dict:
        """Move cursor to normalized screen position (0-1)."""
        return {"action": CURSOR, "params": {"x": x, "y": y}}

    @staticmethod
    def click(button: str = "left") -> dict:
        """Click at current cursor position. button: 'left' or 'right'."""
        return {"action": CLICK, "params": {"button": button}}
