import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarehouseDashboardComponent } from './warehouse-dashboard';

describe('WarehouseDashboard', () => {
  let component: WarehouseDashboardComponent;
  let fixture: ComponentFixture<WarehouseDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WarehouseDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WarehouseDashboardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
