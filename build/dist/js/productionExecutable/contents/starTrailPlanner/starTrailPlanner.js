$(function () {
  $("#timeOfShooting").flatpickr({
    enableTime: true,
    dateFormat: "Y-m-d H:i",
  });
  $("#targetDate").flatpickr();

  $("#calcTime").submit(function () {
    var shootingTime = new Date($("#timeOfShooting").val());
    var targetDate = new Date($("#targetDate").val() + " 00:00");
    var standard = new Date(new Date().getFullYear(), 0, 1);

    if (
      shootingTime.toString() == "Invalid Date" ||
      targetDate.toString() == "Invalid Date"
    ) {
      alert("Invalid Date!");
      return false;
    }

    try {
      var shootingPer = getPercentage(shootingTime, standard);
      var targetPer = getPercentage(targetDate, standard);
      var date = getDate(shootingPer, targetPer, targetDate);
      $("#per").text("経過：" + shootingPer * 100 + "%");
      $("#time").text(
        "時間：" +
          (date.getMonth() + 1) +
          "/" +
          date.getDate() +
          " " +
          date.getHours() +
          ":" +
          date.getMinutes()
      );
      $("#result").show();
    } catch (error) {
      console.log(error);
    }
    return false;
  });
});

const hourMillis = 60 * 60 * 1000;
const dateMillis = 24 * hourMillis;
const yearMillis = 365 * dateMillis;

function getPercentage(target, standard) {
  var millis = target.getTime() - standard.getTime();
  var date = Math.floor(millis / dateMillis);
  var time = (millis % dateMillis) / hourMillis;
  return date / 365 + (time - 24) / 24;
}

function getDif(target, shooting) {
  var dateDif =
    Math.floor(shooting.getTime() / dateMillis) -
    Math.floor(target.getTime() / dateMillis);
  return (dateDif * dateMillis) / 365 + (shooting.getTime() % dateMillis);
}

function getDate(shootingPer, targetPer, targetDate) {
  return new Date(
    targetDate.getTime() + (shootingPer - targetPer) * dateMillis
  );
}
