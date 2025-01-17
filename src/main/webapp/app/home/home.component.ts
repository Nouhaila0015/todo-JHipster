import { Component, OnDestroy, OnInit, inject, signal, Renderer2 } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  standalone: true,
  imports: [SharedModule, RouterModule],
})
export default class HomeComponent implements OnInit, OnDestroy {
  account = signal<Account | null>(null);

  private readonly destroy$ = new Subject<void>();
  private readonly renderer = inject(Renderer2);

  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    // Ajouter la classe pour la page d'accueil
    this.renderer.addClass(document.body, 'home-page');

    this.accountService
      .getAuthenticationState()
      .pipe(takeUntil(this.destroy$))
      .subscribe(account => {
        this.account.set(account);
        if (account) {
          this.router.navigate(['/todo']);
        }
      });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  ngOnDestroy(): void {
    // Retirer la classe quand on quitte la page
    this.renderer.removeClass(document.body, 'home-page');
    this.destroy$.next();
    this.destroy$.complete();
  }
}
