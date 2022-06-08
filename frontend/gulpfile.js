var gulp = require('gulp');
var uglify = require('gulp-uglify');

gulp.task('uglify', function() {
  return gulp.src(['./dist/**/*.js'])
    .pipe(uglify())
    .pipe(gulp.dest('./dist'));
});
