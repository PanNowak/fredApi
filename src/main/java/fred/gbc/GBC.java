package fred.gbc;

import java.awt.*;

public class GBC extends GridBagConstraints {
    /**
     * Creates GBC object with given gridx and gridy values
     * and all of the rest parameters with default values.
     * @param gridx gridx coordinate
     * @param gridy gridy coordinate
     */
    public GBC(int gridx, int gridy) {
        this.gridx = gridx;
        this.gridy = gridy;
    }

    /**
     * Creates GBC object with given gridx, gridy, gridwidth and gridheight values
     * and all of the rest parameters with default values.
     * @param gridx gridx coordinate
     * @param gridy gridy coordinate
     * @param gridwidth number of cells filled horizontally
     * @param gridheight number of cells filled vertically
     */
    public GBC(int gridx, int gridy, int gridwidth, int gridheight) {
        this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
    }

    /**
     * Sets anchor value.
     * @param anchor anchor value
     * @return this object
     */
    public GBC setAnchor(int anchor) {
        this.anchor = anchor;
        return this;
    }

    /**
     * Sets fill direction.
     * @param fill fill direction
     * @return this object
     */
    public GBC setFill(int fill) {
        this.fill = fill;
        return this;
    }

    /**
     * Sets weight values.
     * @param weightx weight horizontal value
     * @param weighty weight vertiacal value
     * @return this object
     */
    public GBC setWeight(double weightx, double weighty) {
        this.weightx = weightx;
        this.weighty = weighty;
        return this;
    }

    /**
     * Sets cell's additional empty space.
     * @param distance inset value in every direction
     * @return this object
     */
    public GBC setInsets(int distance) {
        this.insets = new Insets(distance, distance, distance, distance);
        return this;
    }

    /**
     * Set cell's insets.
     * @param top distance from top edge
     * @param left distance from left edge
     * @param bottom distance from bottom edge
     * @param right distance from right edge
     * @return this object
     */
    public GBC setInsets(int top, int left, int bottom, int right) {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    /**
     * Set cell's internal padding.
     * @param ipadx horizontal internal padding
     * @param ipady vertical internal padding
     * @return this object
     */
    public GBC setIpad(int ipadx, int ipady)
    {
        this.ipadx = ipadx;
        this.ipady = ipady;
        return this;
    }
}
