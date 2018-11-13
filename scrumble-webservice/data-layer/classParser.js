module.exports = (data, targetClass) => {
    if (data.length < 1) {
        throw {"message":"Keine Werte zu dieser Anfrage"};
    } else {
        return data.map(e => new targetClass(...e));
    }
};