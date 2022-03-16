import { APP_INITIALIZER, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from "./sites/main/main.component";



const initializer = (): (() => Promise<void>) => {
  let theme = localStorage.getItem('user_theme')
  if (!theme) {
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      theme = 'dark';
    } else {
      theme = 'light';
    }
    localStorage.setItem('user_theme', theme);
  }

  document.documentElement.setAttribute('data-theme', theme);
  return () => Promise.resolve();
};

const routes: Routes = [
  {
    path: '',
    component: MainComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
    },
  ]
})
export class AppRoutingModule { }
