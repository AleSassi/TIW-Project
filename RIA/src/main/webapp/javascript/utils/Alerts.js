function Alert() {
    this.variantID = 0;

    this.present = function(descriptiveText, defaultPromptText, callback) {
        if (this.variantID === 1) {
            callback(window.confirm(descriptiveText));
        } else if (this.variantID === 2) {
            callback(window.prompt(descriptiveText, defaultPromptText));
        } else {
            window.alert(descriptiveText);
        }
    }

}