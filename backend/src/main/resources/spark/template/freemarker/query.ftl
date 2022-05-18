<#assign content>
  <div class="main-content">
    <div class="row">
      <div class="col-lg-6">
        <div class="content-box">
          <form method="POST" action="/results">
            <p>Algorithm:</p>
            <input type="radio" id="neighbors" name="algorithm" value="neighbors" ${neighbors}>
            <label for="neighbors">
              K Nearest Neighbors
              <input type="text" id="k" name="k" placeholder="k - Integer" value=${k}>
            </label><br>
            <input type="radio" id="radius" name="algorithm" value="radius" ${radius}>
            <label for="radius">
              Radius Search
              <input type="text" id="r" name="r" placeholder="r - Double" value=${r}>
            </label><br>

            <hr>

            <p>Data Type:</p>
            <input type="radio" id="kd" name="type" value="" ${kd}>
            <label for="kd">K-D Tree (Recommended)</label><br>
            <input type="radio" id="naive" name="type" value="naive_" ${naive}>
            <label for="naive">ArrayList (Naive)</label><br>

            <hr>

            <p>Input Type:</p>
            <input type="radio" id="name" name="input" value="star-name" ${nameSelected}>
            <label for="name">
              <label for="star-name">Star</label>
              <input type="text" id="star-name" name="star-name" placeholder="Name" value=${name}>
            </label><br>
            <input type="radio" id="coords" name="input" value="coords" ${coordinates}>
            <label for="coords">
              Coordinates
                <input class="coord" type="text" id="x-coord" name="x-coord" placeholder="X" value=${x}>
                <input class="coord" type="text" id="y-coord" name="y-coord" placeholder="Y" value=${y}>
                <input class="coord" type="text" id="z-coord" name="z-coord" placeholder="Z" value=${z}>
            </label>


            <br>

            <input type="submit" value="Search">
          </form>
        </div>
      </div>

      <div class="col-lg-6">
        <div class="results content-box">
          ${stars}
        </div>
      </div>
    </div>
  </div>

  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>
  <br>


</#assign>
<#include "main.ftl">